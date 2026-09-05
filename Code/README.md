# Recipe Meal Planner (Android Studio project)

Kotlin + Jetpack Compose, Room database, MVVM + Repository pattern.

## Folder structure
```
app/src/main/java/com/example/recipemealplanner/
├── data/
│   ├── local/          # Room entities, DAOs, database
│   └── repository/     # Repository layer
├── ui/
│   ├── theme/           # Compose Material3 theme
│   ├── navigation/       # NavHost + bottom navigation
│   ├── recipe/           # List, detail, add/edit screens + ViewModel
│   ├── mealplanner/       # Weekly planner screen + ViewModel
│   └── shopping/         # Shopping list + pantry screen + ViewModel
└── util/                 # Shared date + image-storage helpers
```

See the repo root `README.md` for team info, environment requirements, and how to run.
