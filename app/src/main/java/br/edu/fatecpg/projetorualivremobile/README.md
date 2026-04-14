# RuaLivre — App Mobile (MVVM + Hilt + Jetpack Compose)

## Arquitetura

```
MVVM (Model - View - ViewModel)
│
├── View (Composables)      → Exibe UI, observa StateFlow do ViewModel
├── ViewModel               → Lógica de apresentação, expõe UiState
├── Repository              → Regra de negócio, orquestra fontes de dados
├── Data Source (API)        → Busca dados remotos (Retrofit / Fake)
└── DI (Hilt)               → Injeta dependências automaticamente
```

**Fluxo de dados (unidirecional):**

```
API → Repository → ViewModel (StateFlow) → Composable (collectAsState)
                                ↑
                          Eventos do usuário
```

## Estrutura de arquivos

```
com.rualivre/
├── RuaLivreApp.kt                          # @HiltAndroidApp
├── MainActivity.kt                         # @AndroidEntryPoint
│
├── di/
│   └── AppModule.kt                        # Hilt module (provê API)
│
├── data/
│   ├── model/
│   │   └── Models.kt                       # Data classes e enums
│   ├── remote/
│   │   └── RuaLivreApi.kt                  # Interface API + FakeApiService
│   └── repository/
│       └── Repositories.kt                 # AuthRepository, AlagamentoRepository
│
├── navigation/
│   └── NavHost.kt                          # Compose Navigation + rotas
│
└── ui/
    ├── theme/
    │   └── Theme.kt                        # Cores, tipografia, shapes
    ├── components/
    │   └── Components.kt                   # BottomBar, TextField, Button, etc.
    └── screens/
        ├── splash/
        │   └── SplashScreen.kt             # Sem ViewModel (tela estática)
        ├── login/
        │   ├── LoginViewModel.kt           # Estado: usuario, senha, loading, error
        │   └── LoginScreen.kt
        ├── register/
        │   ├── RegisterViewModel.kt        # Estado + validação de campos
        │   └── RegisterScreen.kt
        ├── home/
        │   ├── HomeViewModel.kt            # Carrega resumo + alertas
        │   └── HomeScreen.kt
        ├── map/
        │   ├── MapViewModel.kt             # Carrega pontos de alagamento
        │   └── MapScreen.kt
        ├── dashboard/
        │   ├── DashboardViewModel.kt       # Carrega dados de gráficos
        │   └── DashboardScreen.kt
        └── profile/
            ├── ProfileViewModel.kt         # Dados do usuário + logout
            └── ProfileScreen.kt
```

## Dependências (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Activity
    implementation("androidx.activity:activity-compose:1.9.3")

    // Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Google Maps (opcional, para MapScreen)
    // implementation("com.google.maps.android:maps-compose:4.3.3")
    // implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Retrofit (quando conectar API real)
    // implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}`
```

## AndroidManifest.xml

```xml
<application
    android:name=".RuaLivreApp"
    ...>
    <activity
        android:name=".MainActivity"
        android:exported="true"
        android:theme="@style/Theme.RuaLivre">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## Fontes

Baixe a família **DM Sans** do Google Fonts e coloque em `res/font/`:

```
res/font/
├── dm_sans_regular.ttf
├── dm_sans_medium.ttf
├── dm_sans_semibold.ttf
├── dm_sans_bold.ttf
└── dm_sans_extrabold.ttf
```

## Navegação

```
Splash (2s) → Login ↔ Register
                ↓
              Home ←→ Map
                ↕
            Dashboard
                ↕
             Profile → Logout → Login
```

- Bottom Bar aparece em: Home, Map, Dashboard, Profile
- Splash auto-navega após 2 segundos
- Logout limpa o repositório e volta para Login

## Como migrar para API real

1. **Crie a interface Retrofit** em `data/remote/RuaLivreApi.kt` com anotações `@GET`, `@POST`
2. **Atualize o `AppModule`** para prover `Retrofit` e a implementação real da API
3. **Remova o `FakeApiService`** — os Repositories e ViewModels continuam iguais
4. Os Composables não mudam nada (só observam o StateFlow)

## Princípios seguidos

- **Single Source of Truth**: estado vive no ViewModel via `StateFlow`
- **Unidirectional Data Flow**: UI observa estado, envia eventos ao ViewModel
- **Separation of Concerns**: Composables não têm lógica de negócio
- **Dependency Injection**: Hilt injeta API → Repository → ViewModel
- **Testabilidade**: Repositories recebem interface, fácil de mockar em testes
