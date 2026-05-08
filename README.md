# CVP — Capa Vial Predictiva

Muestra zonas de Buenos Aires con historial registrado de siniestros viales sobre un mapa interactivo,
para que los conductores estén más atentos al volante. Trabaja con datos públicos del Observatorio de
Movilidad y Seguridad Vial de la Ciudad Autónoma de Buenos Aires.

> El conductor humano no es defectuoso, es limitado. CVP recuerda por él lo que los datos ya saben.

---

## Capturas de pantalla

| Mapa principal | Detalle de zona | Configuración | Onboarding |
|:-:|:-:|:-:|:-:|
| _placeholder_ | _placeholder_ | _placeholder_ | _placeholder_ |

_(Las capturas se agregan después de la primera distribución interna.)_

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2 con K2 compiler |
| UI | Jetpack Compose + Material 3 |
| Navegación | Compose Navigation 2.8 (type-safe, serialización) |
| DI | Koin 4 |
| Async | Kotlin Coroutines + Flow |
| Serialización | kotlinx.serialization |
| Fecha/hora | kotlinx-datetime |
| Persistencia | DataStore Preferences |
| Mapa | Mapbox Maps SDK 11.x |
| Ubicación | Google Play Services Location (Fused Location Provider) |
| Imágenes | Coil 3 |
| Logging | Timber (solo en debug) |
| Build | Gradle Kotlin DSL + Version Catalog |
| Minificación | R8 (habilitado en release) |

---

## Requisitos previos

- Android Studio Meerkat o superior
- JDK 17
- Una cuenta en [mapbox.com](https://account.mapbox.com/) para obtener los tokens

---

## Configurar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/<owner>/BuenViaje.git
cd BuenViaje
```

### 2. Crear `local.properties`

```bash
cp local.properties.template local.properties
```

Editá el archivo resultante. Los campos obligatorios para compilar:

```properties
sdk.dir=/Users/<tu-usuario>/Library/Android/sdk
MAPBOX_DOWNLOADS_TOKEN=sk.eyJ...   # scope DOWNLOADS:READ
MAPBOX_ACCESS_TOKEN=pk.eyJ...      # scopes styles:read, tiles:read, fonts:read
```

Para builds de release, agregá también las credenciales del keystore (ver sección [Firmar el APK](#firmar-el-apk)).

### 3. Ubicar el dataset

Copiá el archivo `risk_zones.geojson` generado desde el repositorio Python de procesamiento de datos en:

```
app/src/main/assets/risk_zones.geojson
```

Este archivo **no está incluido en el repositorio** porque es un asset regenerable. El proyecto incluye un
dataset de muestra minimal para compilar. Para producción, usá el dataset completo del Observatorio.

> El formato esperado es GeoJSON FeatureCollection con puntos (Point). Cada feature debe tener las
> propiedades: `id`, `incident_count`, `leve_count`, `grave_count`, `mortal_count`,
> `predominant_hour_range`, `predominant_weekday`, `via_type`, `predominant_victim_mode`,
> `address_label`, `comuna`, `radius_meters`. Todos los campos son opcionales (pueden ser `null` o
> `"NA"`); el mapper aplica defaults sensatos.

---

## Compilar

### Debug (desarrollo)

```bash
# Solo compilar
./gradlew :app:assembleDebug

# Compilar e instalar en el dispositivo/emulador conectado
./gradlew :app:installDebug
```

O usá **Run ▶** en Android Studio.

### Release (distribución)

#### Firmar el APK

1. Generá un keystore (solo la primera vez):

```bash
keytool -genkey -v -keystore cvp-release.jks \
  -alias cvp -keyalg RSA -keysize 2048 -validity 10000
```

2. Completá las credenciales en `local.properties`:

```properties
STORE_FILE=../cvp-release.jks
STORE_PASSWORD=contraseña_del_keystore
KEY_ALIAS=cvp
KEY_PASSWORD=contraseña_de_la_clave
```

3. Compilá el APK o el AAB:

```bash
# APK firmado
./gradlew :app:assembleRelease

# Android App Bundle (para subir a Google Play)
./gradlew :app:bundleRelease
```

Los artefactos se generan en `app/build/outputs/`.

---

## Correr tests

### Tests unitarios (sin dispositivo)

```bash
./gradlew test
```

Cubre: `GeohashTest`, `GeohashIndexTest`, `DistanceUtilsTest`, `DetectNearbyZonesUseCaseTest`,
`ShouldNotifyUseCaseTest`, `SettingsRepositoryTest`, `GeoJsonLoaderTest`, `RiskZoneMapperTest`,
`RiskZoneRepositoryTest`, `SeverityTest`.

### Tests de instrumentación (requiere dispositivo o emulador)

```bash
./gradlew connectedAndroidTest
```

Cubre: `MapScreenTest`, `OnboardingScreenTest`, `ZoneDetailBottomSheetTest`.

### Lint

```bash
./gradlew lint
```

### Todo junto (recomendado antes de hacer push)

```bash
./gradlew test lint
```

---

## Estructura de paquetes

```
com.cvp.app
├── core
│   └── design
│       ├── animations/   # Constantes de animación (duraciones, easing)
│       ├── components/   # Componentes reutilizables: CvpButton, CvpCard, CvpEmptyState…
│       └── theme/        # CvpTheme, colores, tipografía (Inter), espaciado, formas
├── data
│   ├── local
│   │   ├── dto/          # DTOs de GeoJSON para kotlinx.serialization
│   │   ├── mapper/       # RiskZoneMapper: DTO → domain model
│   │   └── preferences/  # OnboardingPreferences (DataStore)
│   ├── location/         # LocationService: Fused Location Provider → Flow<UserLocation>
│   └── repository/       # Implementaciones: RiskZoneRepositoryImpl, SettingsRepositoryImpl
├── di/                   # Módulos de Koin (AppModule, DataModule, PresentationModule…)
├── domain
│   ├── model/            # RiskZone, Severity, UserLocation, ProximityEvent, AppSettings
│   ├── repository/       # Interfaces de repositorio (domain layer)
│   ├── usecase/          # DetectNearbyZonesUseCase, ShouldNotifyUseCase
│   └── util/             # DistanceUtils (Haversine), Geohash, GeohashIndex
└── presentation
    ├── common/           # LocationPermissionHandler
    ├── map/              # MapScreen, MapViewModel, CvpMapState, RiskZoneRenderer
    ├── onboarding/       # OnboardingScreen, OnboardingViewModel
    ├── settings/         # SettingsScreen, SettingsViewModel, AboutScreen, PrivacyScreen
    ├── welcome/          # WelcomeScreen (standalone, no onboarding)
    └── zonedetail/       # ZoneDetailBottomSheet
```

---

## Arquitectura

CVP sigue **Clean Architecture** en tres capas:

```
data ──────▶ domain ◀────── presentation
```

- **domain**: modelos, interfaces de repositorio, casos de uso, utilidades. Sin dependencias de Android.
- **data**: implementaciones de repositorio, loaders, DTOs. Depende de Android (Context, DataStore).
- **presentation**: ViewModels + Composables. Solo habla con domain.

La inyección de dependencias es **Koin**. Los módulos están en `di/`.

---

## Manejo de errores

| Escenario | Comportamiento |
|---|---|
| GeoJSON corrupto o ilegible | `MapScreen` muestra `CvpEmptyState` con botón "Reintentar" |
| Permisos de ubicación denegados | Card flotante en el mapa invitando a reactivarlos; el mapa sigue funcionando |
| Permisos revocados en runtime | La app sigue operativa sin la posición del usuario |
| Error transitorio de ubicación | Snackbar con mensaje |

---

## Roadmap

- **Filtros por severidad**: botón toggle en el mapa para mostrar solo zonas HIGH/MEDIUM/LOW.
- **Modo navegación**: alertas de audio al acercarse a zonas de riesgo mientras se conduce.
- **Notificaciones en segundo plano**: `ForegroundService` para alertas con la pantalla apagada.
- **Dataset actualizable**: descarga incremental del dataset desde un CDN en lugar de asset bundled.
- **Soporte iOS**: reimplementación en SwiftUI con la misma lógica de dominio (compartida vía KMP).
- **Clusters**: agrupación de zonas cercanas en el mapa a zoom bajo para reducir ruido visual.
- **Modo offline total**: cacheo de tiles del mapa para uso sin conexión.

---

## Privacidad

CVP no recopila, almacena ni transmite datos personales. La ubicación del usuario nunca sale del
dispositivo. Ver la [política de privacidad completa](docs/privacy.html).

---

## Licencia

MIT License — ver [LICENSE.md](LICENSE.md).
