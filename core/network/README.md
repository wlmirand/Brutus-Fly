# Network transport contract

Pure Kotlin/JVM module containing `ApiResult`, `ApiError` and Retrofit's
`ApiResultCallAdapterFactory`. It has no Android, Firefly, persistence or UI dependencies.

## Retrofit registration

```kotlin
Retrofit.Builder()
    .baseUrl(baseUrl)
    .addCallAdapterFactory(ApiResultCallAdapterFactory())
    .addConverterFactory(json.asConverterFactory(contentType))
```

Endpoints may return `suspend fun get(): ApiResult<MyDto>` or
`fun get(): Call<ApiResult<MyDto>>`. Unwrapped Retrofit endpoints are left alone.

## Semantics

- `Success`: a 2xx response with a non-null converted payload, status and headers.
- `Error(Http)`: a non-2xx response with status, headers and an optional error body.
- `Error(Network)`: an `IOException`, including transport or response-body read failures.
- `Error(Serialization)`: invalid kotlinx.serialization payloads or missing required bodies.
- `ApiResult<Unit>` accepts successful empty responses, including 204/205.
- Empty responses for other body types are failures, not successes with null data.
- Cancellation cancels the underlying call and propagates rather than becoming an error value.
- Unexpected exceptions propagate. Domain mapping is never performed by the adapter.

The adapter reads raw response bodies so it can classify conversion errors. Actual
HTTP metadata is in `ApiResult`, not in the synthetic successful Retrofit `Response`
used internally to deliver error values through the suspend bridge.

Only up to 64 KiB of HTTP error-body bytes are retained as text. The truncation flag
indicates omitted bytes; a failed read leaves the body null while preserving the
known HTTP status. Retrofit may buffer the response upstream, so this is not a
global response-size limit. Bodies are closed after conversion or inspection.

Never log results or response bodies indiscriminately. The HTTP error's `toString`
omits headers and body, but payloads and exception messages may still be sensitive.

## Repository boundary

Repositories consume `Success`/`Error`, map DTOs and translate `ApiError` into their
own domain failures using dedicated `Mapper` implementations. Use cases and
ViewModels do not receive `ApiResult`, HTTP headers or response codes.

The Firefly API factory registers this adapter. Hilt selects the HTTP-backed
transaction repository, which uses the saved server configuration.

## Tests

```sh
./gradlew :core:network:test --configure-on-demand
```

Tests use synthetic OkHttp responses, not a live server, and cover successful
payloads, metadata, HTTP errors, bounded error bodies, IO, JSON conversion, Unit,
empty/null payloads, synchronous calls, cloning and coroutine cancellation.
