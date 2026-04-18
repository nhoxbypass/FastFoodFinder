---
trigger: always_on
---

## Android: Tech Stack & Libraries

- Language: Kotlin. Use idiomatic Kotlin features (extension functions, scoped functions, data classes, sealed interfaces)
- UI Toolkit: Jetpack Compose. DO NOT use XML layouts, Fragments, or ViewBinding.
- Asynchronous Programming: Kotlin Coroutines and StateFlow/SharedFlow. DO NOT use RxJava or LiveData.
- Dependency Injection: Hilt.
- Networking: Retrofit with OkHttp and Kotlinx Serialization.