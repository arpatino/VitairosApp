# VITAIROS 

---

## 👥 Integrantes del Equipo
* Arlette Patiño 
* Victoria Ortega

---

## 📋 Resumen del Avance 1
Este repositorio contiene el **Primer Avance ** del desarrollo de la aplicación móvil industrial **VITAIROS Industrial AI**, diseñada para la captura estructurada de reportes en faenas mineras e industriales en condiciones de conectividad intermitente (*offline-first*).

En este avance se implementan las bases sólidas de la arquitectura limpia, la persistencia local en base de datos SQLite con Room, el acceso nativo a la conectividad del dispositivo en tiempo real, el flujo de autenticación por roles con persistencia de sesión segura en DataStore, el panel de control (Dashboard) con telemetría industrial en vivo, y el formulario completo de ingreso de reportes con validación campo por campo y retroalimentación animada.


---

## 🏗️ Arquitectura por Capas

El proyecto sigue los principios de **Clean Architecture** estructurado en un único módulo `:app` con estricta dirección de dependencias:
$$\text{UI} \longrightarrow \text{Domain} \longleftarrow \text{Data}$$

```
cl.vitairos.industrial/
├── data/                       # Capa de Datos (Implementaciones concretas)
│   ├── local/
│   │   ├── db/                 # Room SQLite: Database, DAO, Entities, Converters
│   │   ├── files/              # Almacenamiento privado (PrivateFileStore)
│   │   └── prefs/              # DataStore de sesión cifrada/simulada
│   ├── remote/                 # API Mock y DataSource remoto
│   ├── repository/             # Implementaciones de Repositorios
│   └── sync/                   # Telemetría de red (ConnectivityObserver) y WorkManager
├── domain/                     # Capa de Dominio (Kotlin puro, sin dependencias de Android)
│   ├── model/                  # Entidades de negocio (Report, User, Process, Attachment)
│   ├── repository/             # Interfaces abstractas de repositorios
│   └── usecase/                # Casos de uso de negocio (SaveDraft, DeleteReport)
├── di/                         # Contenedor de Inyección de Dependencias
│   └── AppContainer.kt         # Inyección manual transparente y explicable
└── ui/                         # Capa de Presentación (Jetpack Compose)
    ├── components/             # Componentes reusables (PrimaryButton, StatusChip, SimulationBadge)
    ├── navigation/             # AppNavHost y Routes
    ├── screens/
    │   ├── login/              # LoginScreen + LoginViewModel (Autenticación y selección de roles)
    │   ├── dashboard/          # DashboardScreen + DashboardViewModel (Telemetría y resumen Room)
    │   └── newreport/          # NewReportScreen + NewReportViewModel (Formulario validado)
    └── theme/                  # Paleta oficial industrial de 8 colores y tipografía
```

---

## 🎨 Identidad Visual Industrial

La aplicación cumple estrictamente con el manual de identidad visual de VITAIROS utilizando únicamente los 8 colores autorizados:

* **Verde Noche (`#081D16`):** Fondo principal oscuro de alta concentración para terreno.
* **Verde Profundo (`#102B23`):** Superficies de tarjetas y contenedores de datos.
* **Verde Mineral (`#1B342D`):** Bordes estructurales, divisores y contrastes secundarios.
* **Hueso (`#FBF7F1`):** Tipografía principal de alta legibilidad.
* **Niebla (`#E8EFE9`):** Tipografía secundaria y etiquetas descriptivas.
* **Cobre (`#DD7A52`):** Acento primario, botones de acción principal e indicadores de atención.
* **Oro (`#D9B46C`):** Acento secundario para estados en cola y avisos de telemetría.
* **Acero (`#AAB6B7`):** Metadatos técnicos, marcas de tiempo y textos de apoyo.

---

### Compilación por Consola :
```powershell
cd c:\Users\arlet\Desktop\DesAppsMoviles\Vitairos\Vitairos_Avance1
.\gradlew.bat assembleDebug
```
El archivo ejecutable generado se ubicará en:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔑 Credenciales de Prueba (Demo)

Para evaluar el sistema de inicio de sesión con control de roles:

| Rol | Usuario | Contraseña | Perfil y Acceso |

| **Operador de Terreno** | `u-operador` | `operador123` | Acceso completo a creación de reportes, telemetría y cola local. |
| **Supervisor de Planta** | `u-supervisor` | `supervisor123` | Vista de supervisión, validación de reportes y auditoría técnica. |


---

## 🗺️  Avances del Proyecto

  - Flujo de autenticación con roles diferenciados.
  - Dashboard industrial con telemetría de red nativa en vivo.
  - Formulario de Nuevo Reporte con validación reactiva campo por campo e iconos visuales.
  - Persistencia funcional en base de datos SQLite mediante Room.
  - Arquitectura modular limpia (UI, Domain, Data) con manual DI.

