# Cookie Munch Java SDK

A small, typed Java client for the [Cookie Munch](https://cookiemunch.net) Developer API
(the `/v1` surface) plus the public consent-ingest endpoint (`POST /api/v1/consent`).

- **Java 17+**, built with Maven.
- HTTP via the JDK's built-in `java.net.http.HttpClient` — no HTTP dependency.
- One runtime dependency: **Jackson** (`jackson-databind`) for JSON binding.
- Immutable `record` types for every entity; `CookieMunchApiException` carries the HTTP
  status, response body, and the server's `error`/`code` fields.

## Requirements & building

Requires **JDK 17+**. Build and run the tests with:

```bash
mvn -q -DskipTests=false test
```

(Just compile: `mvn -q -DskipTests package`.) The test suite is self-contained — the unit
tests use an injected in-memory HTTP layer and the integration test spins up the JDK's
built-in `com.sun.net.httpserver.HttpServer` on an ephemeral loopback port, so nothing
reaches the network and it is safe to run in CI.

## Coordinates

Maven:

```xml
<dependency>
  <groupId>net.cookiemunch</groupId>
  <artifactId>cookiemunch</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
implementation("net.cookiemunch:cookiemunch:0.1.0")
```

Gradle (Groovy DSL):

```groovy
implementation 'net.cookiemunch:cookiemunch:0.1.0'
```

## Usage

```java
import net.cookiemunch.CookieMunch;
import net.cookiemunch.CookieMunchApiException;
import net.cookiemunch.model.*;

import java.util.List;

// The org is derived server-side from the key — you never pass an orgId.
CookieMunch cm = new CookieMunch("fck_live_...");

// ...or configure the base URL / transport via the builder:
CookieMunch cm2 = CookieMunch.builder("fck_live_...")
    .baseUrl("https://cmp.example.com") // default: https://api.cookiemunch.net
    .build();

// Every request sends both `Authorization: Bearer <key>` and `X-API-Key: <key>`.

Identity me = cm.me();
List<Site> sites = cm.sites().list();
Site site = cm.sites().create(new SiteCreate("example.com"));

// Per-site consent analytics.
List<ConsentDay> stats = cm.consent().stats(site.cbid(), null, null);
String csv = cm.consent().export(site.cbid());          // raw CSV string
Map<String, Object> receipt = cm.consent().receipt(site.cbid(), "stamp-abc");

// Log a consent decision via the public ingest endpoint (POST /api/v1/consent, 204).
cm.logConsent(ConsentIngest.builder(site.cbid())
    .stamp("stamp-abc")
    .choices(true, false, true)   // preferences, statistics, marketing
    .method("explicit")
    .ver(1)
    .utc(System.currentTimeMillis())
    .url("https://example.com/")
    .build());

// Governance surfaces.
cm.dsar().create(new DsarCreate("access", "person@example.com", "gdpr"));
cm.vendors().create(new VendorInput("Acme", "analytics", List.of("email"), true, 2, List.of("iso27001"), "eu"));
cm.banners().publish("banner_id");
```

### Error handling

Any non-2xx response throws `CookieMunchApiException` (an unchecked `RuntimeException`):

```java
try {
    cm.sites().create(new SiteCreate("dup.com"));
} catch (CookieMunchApiException e) {
    e.getStatusCode(); // e.g. 409
    e.getMessage();    // the server's `error` field, when present
    e.getCode();       // the server's optional `code`, e.g. "banner_in_use" (may be null)
    e.getBody();       // the raw response body
}
```

Transport failures and JSON encode/decode errors throw the base `CookieMunchException`.

### Custom HTTP layer / testing

`CookieMunch.builder(key).transport(HttpTransport)` injects a custom HTTP layer. The
`HttpTransport` interface is a single `send(Request) -> Response` method, which makes the
client trivial to unit-test without a socket (see `src/test/java`).

## API surface

| Group | Methods |
|---|---|
| top-level | `me()`, `usage()`, `audit(Integer limit)`, `logConsent(ConsentIngest)` |
| `sites()` | `list`, `create`, `get`, `delete`, `getConfig`, `putConfig`, `patchConfig`, `cookies`, `scan`, `scanStatus`, `ab`, `snippet`, `verify`, `brand`, `getFlow`, `editFlow`, `setFlow`, `enableAdPersonalization`, `banner`, `policy` (Markdown `String`), `analyzeSession` |
| `consent()` | `stats`, `log`, `export`, `receipt`, `eraseSubject`, `exportSubject` |
| `dsar()` | `list`, `create`, `advance`, `response` (plain-text notice), `erase`, `export` |
| `vendors()` | `list`, `create` |
| `ropa()` | `list`, `create`, `exportCsv` |
| `brandKits()` | `list`, `create`, `delete` |
| `preferences()` | `list`, `save`, `get` |
| `members()` | `list`, `invite`, `setRole`, `remove` |
| `keys()` | `list`, `issue` — `new ApiKeyIssueInput(name, scopes, cbids, expiresInDays)` for a least-privilege key —, `revoke`, `roll`, `update` |
| `webhooks()` | `list`, `create`, `delete`, `update`, `rollSecret`, `test`, `deadLetters`, `replayDeadLetter` |
| `banners()` | `list`, `create`, `get`, `update`, `delete`, `assignments`, `setAssignments`, `publish` |
| `org()` | `get`, `update` — requires an unscoped key that is not property-locked |
| `assets()` | `upload` — requires `sites:write` |
| `identity()` | `resolve`, `link`, `cluster` |
| `vault()` | `record`, `current`, `permits` |
| `profile()` | `get`, `setAttributes`, `activate` |
| `subscriptions()` | `topics`, `setTopics`, `get`, `set`, `unsubscribeAll`, `resubscribe`, `activation` |
| `assessments()` | `templates`, `list`, `start`, `get`, `answer`, `autoPopulateFromMap`, `autoPopulate`, `submit`, `approve`, `reject` |
| `discovery()` | `ingestMap`, `getMap`, `ropaDrafts`, `evidence`, `drift`, `planEnforcement` |
| `ai()` | `getPolicy`, `setPolicy`, `inspect`, `inventory`, `lineage`, `registerSystem`, `systems`, `audit` |
| `fulfillment()` | `sla`, `plan`, `status`, and for the in-environment agent `pendingTasks`, `reportTask` |
| `regulatory()` | `feed`, `upcoming` |
| `reseller()` | `list`, `create`, `get`, `update`, `deprovision` (suspends; `purge=true` deletes irreversibly), `listKeys`, `mintKey`, `revokeKey` — needs the `reseller:*` scopes |

Every operation of the `/v1` API is reachable; `ParityTest` keeps it that way against
`sdks/operations.json`, generated from the server's OpenAPI document. Identity, vault and
profile reads are `POST`s so a person's identifiers never appear in a URL.

The client's JSON encoding omits `null` fields, which is right for optional record fields.
Where `null` means something — `reseller().update(id, Map.of(...))` with a `"dsarRouting"`
entry mapped to `null` clears that override — the call keeps it.

Where the published TypeScript SDK's aspirational types disagreed with the server's
OpenAPI schema (`packages/server/src/openapi-schemas.ts`), the wire shape wins so these
records decode real responses. Notable cases: `ScanStatus` is `{ status, lastScannedAt }`
(not the richer `ScanResult`); `AbResult` uses `optIns` (not `optIn`/`optOut`); `Usage` is
`{ domains, seats, monthlyEvents }`; `Member` is keyed by `userId`; `keys().list()` returns
`ApiKeyPrefix` and `keys().issue()` returns `ApiKeyIssued`; site cookies come back as a
`CookieDeclaration { updatedAt, cookies }`.

## License

MIT.
