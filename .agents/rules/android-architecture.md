---
trigger: always_on
---

## Android: Architecture

- Follow Clean Architecture and Google's official architecture for Android apps
- ViewModels must NOT contain any Android framework references (no `Context`, `View`, etc.)