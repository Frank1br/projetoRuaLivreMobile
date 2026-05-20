# RuaLivre — App Mobile

Projeto de Extensão · Fatec 2026/1

App Android que monitora alagamentos urbanos em tempo real, consumindo uma API com detecção por IA (YOLO) sobre câmeras de monitoramento.

| Componente | Repositório | Tag atual |
|---|---|---|
| App mobile | [Frank1br/projetoRuaLivreMobile](https://github.com/Frank1br/projetoRuaLivreMobile) | `v1.0.0` |
| API + IA   | [Frank1br/projetoRuaLivreIA](https://github.com/Frank1br/projetoRuaLivreIA)         | `v1.1.0` |

---

## Visão geral

```
 ┌────────────────┐        HTTP/JSON         ┌─────────────────────┐
 │   App Android  │  ◄────────────────────►  │  API FastAPI + IA   │
 │   (Kotlin /    │      Bearer JWT          │  YOLO · PostgreSQL  │
 │    Compose)    │                          │  Paraconsistente    │
 └────────────────┘                          └─────────────────────┘
        ▲                                              ▲
        │ OSMDroid (OpenStreetMap)                     │ Frames de câmeras
        ▼                                              ▼
 Mapa interativo                                Detecção (cobertura
 + Dashboard                                    de água em %)
```

## Stack técnica

| Camada | Tecnologias |
|---|---|
| App   | Kotlin · Jetpack Compose · Material 3 · Hilt (DI) · Retrofit · OkHttp · OSMDroid · EncryptedSharedPreferences |
| API   | FastAPI · SQLAlchemy · Pydantic v2 · python-jose (JWT) · passlib (bcrypt) · slowapi (rate limit) |
| IA    | Ultralytics YOLO · OpenCV · análise paraconsistente (LPA2v) · IPP (Índice de Periculosidade por Ponto) |
| Banco | PostgreSQL |

---

## O que foi entregue

### App mobile · v1.0.0

#### Correções estruturais

| Problema | Solução |
|---|---|
| Login retornava 401 sempre | Label do campo era "Usuário" mas a API pede email. Normalização (trim + lowercase) no `AuthRepository`. |
| App fechava na HomeScreen após login | Modelos `Alerta` e `Alagamento` foram construídos contra um contrato de API inexistente. `nivel` ficava `null` → `NullPointerException` no `when()`. Realinhamento completo dos `data class`. |
| Mapa e Dashboard com dados fictícios | `Camera`, `DashboardStats` e `HistoricoEntry` alinhados ao servidor real. Status da câmera tratado como string (não enum estrito) à prova de null. |
| Token perdia ao fechar o app | Migrado para `EncryptedSharedPreferences`. Splash valida via `/auth/me` e decide Home vs Login. |
| 401 deixava usuário em tela quebrada | Interceptor detecta 401, limpa token e dispara evento que leva ao Login com snackbar "Sessão expirada". |
| Falhas de rede silenciosas | Snackbar global ("Sem conexão com o servidor") emitido pelo interceptor. |

#### Features visíveis

- **Card de cobertura urbana** na Home — "X de Y bairros (Z%) com alagamento ativo · N ocorrências"; estado positivo "Tudo tranquilo" quando 0; chips de severidade com contagem.
- **Mapa interativo** plota alagamentos com coordenadas reais (vindas da câmera associada). Câmeras (quadrado) e alagamentos (círculo) distinguidos visualmente; cor pela gravidade.
- **Dashboard** com dados reais: cards (alagamentos ativos / câmeras ativas / alertas hoje), donut por nível, barras de ocorrências por dia, lista de alagamentos por região.
- **Pull-to-refresh** em Home e Dashboard.
- **Bottom sheet** com detalhe ao tocar num alerta.
- **Ícone customizado** (gota d'água sobre indigo, mesma identidade da splash).

#### Infraestrutura

- `BASE_URL` configurável via `local.properties` → `BuildConfig.API_BASE_URL` (sem hardcoded no código).
- Botões inertes da UI removidos (Editar perfil, Notificações, etc., "Esqueceu a senha?").

### API · v1.1.0

#### Segurança

| Item | Antes | Depois |
|---|---|---|
| CORS | `allow_origins=["*"]` com `allow_credentials=True` | Lista estrita, `allow_credentials=False` |
| `/auth/login` | Sem proteção contra brute-force | Rate limit **5/min por IP** (slowapi) |
| `/auth/register` | Sem proteção | Rate limit **10/min por IP** |
| `/flood/analyze/{camera_id}` | **Anônimo** (qualquer um podia subir frame) | Exige token de admin |
| Validação de email | `str` genérico (aceitava "Frank") | `EmailStr` (Pydantic) — rejeita malformado no schema |

#### Endpoints novos / expandidos

- **`/dashboard/stats`** ganhou `total_bairros_monitorados` (COUNT DISTINCT de bairros nas câmeras) — base da métrica de cobertura urbana no app.
- **`/flood/alagamentos`** agora retorna `latitude`, `longitude`, `bairro`, `municipio` (puxados da câmera associada via `joinedload`). Antes, o app não tinha como plotar alagamentos no mapa.
- **`AlagamentoResponse`** ganhou os 4 campos opcionais acima.

#### Infraestrutura

- `app/limiter.py` separado para evitar import circular.
- `requirements.txt`: adicionados `slowapi`, `email-validator`.

---

## Como rodar o app

### Pré-requisitos

- Android Studio (Hedgehog ou superior)
- Android SDK 35+ (compilação) · minSdk 24 (Android 7.0)
- JDK 11
- API do RuaLivre rodando (ver [repositório da API](https://github.com/Frank1br/projetoRuaLivreIA))

### Configuração

1. Clone o repositório:
   ```bash
   git clone https://github.com/Frank1br/projetoRuaLivreMobile.git
   ```
2. Configure o endereço da API em `local.properties` (arquivo não versionado):
   ```properties
   api.base_url=http://SEU_IP_LAN:8000/
   ```
   Se omitido, o padrão é `http://192.168.2.73:8000/`.
3. Abra no Android Studio, sincronize o Gradle e instale num device:
   ```bash
   ./gradlew installDebug
   ```

### Fluxo de uso

1. **Splash** decide automaticamente entre Login e Home (se há sessão válida).
2. **Cadastro** → cria conta com email válido (`EmailStr` no backend rejeita malformado).
3. **Login** → guarda token JWT em `EncryptedSharedPreferences`.
4. **Home** mostra cobertura urbana atual e alertas recentes.
5. **Mapa** plota câmeras (por status) e alagamentos (por gravidade).
6. **Dashboard** consolida estatísticas e histórico.
7. **Perfil** permite logout.

---

## Estrutura do projeto

```
app/src/main/java/br/edu/fatecpg/projetorualivremobile/
├── MainActivity.kt
├── RuaLivreApp.kt              # Application com @HiltAndroidApp
├── di/AppModule.kt             # Retrofit, OkHttp, interceptor JWT
├── data/
│   ├── model/Models.kt         # Data classes (espelham o contrato da API)
│   ├── remote/RuaLivreApi.kt   # Interface Retrofit
│   └── repository/             # AuthRepository, AlagamentoRepository, etc.
├── navigation/                 # NavHost, AppEventsViewModel
├── util/AuthEventBus.kt        # 401 / erros de rede
└── ui/screens/                 # splash, login, register, home, map, dashboard, profile
```

---

## Tags / Releases

- Mobile · `v1.0.0` → https://github.com/Frank1br/projetoRuaLivreMobile/releases/tag/v1.0.0
- API · `v1.1.0` → https://github.com/Frank1br/projetoRuaLivreIA/releases/tag/v1.1.0
