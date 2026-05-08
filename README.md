<div align="center">

# CVP — Capa Vial Predictiva

**Zonas de riesgo vial de Buenos Aires, en tiempo real, en tu bolsillo.**

Muestra sobre un mapa interactivo las zonas de la Ciudad de Buenos Aires con historial documentado de siniestros viales, alertando al conductor cuando se acerca a una de ellas. Trabaja con datos públicos del [Observatorio de Movilidad y Seguridad Vial de la CABA](https://data.buenosaires.gob.ar/).

> *El conductor humano no es defectuoso, es limitado. CVP recuerda por él lo que los datos ya saben.*

---

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE.md)
[![Android API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.02-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Koin](https://img.shields.io/badge/DI-Koin%204-orange.svg)](https://insert-koin.io/)

</div>

---

## Capturas de pantalla

| Mapa principal | Detalle de zona | Configuración | Onboarding |
|:-:|:-:|:-:|:-:|
| _próximamente_ | _próximamente_ | _próximamente_ | _próximamente_ |

---

## Características

- **Mapa interactivo** — zonas coloreadas por severidad (BAJA / MEDIA / ALTA) sobre MapLibre.
- **Detección de proximidad en tiempo real** — usa geohash como índice espacial para lookups O(1) sin escanear todo el dataset.
- **Alertas direccionales** — solo notifica zonas que están *delante* del conductor (cono ±90°); evita alertas de zonas ya superadas.
- **Sin fatiga de alertas** — cada zona alerta una sola vez por sesión de uso.
- **Detalle de zona** — bottom sheet con estadísticas completas: víctimas, tipo de vía, rango horario predominante, día de la semana.
- **Configuración completa** — radio de alerta (50–300 m), notificaciones, vibración, tema claro/oscuro/sistema, visibilidad de zonas.
- **Onboarding** — flujo introductorio en el primer arranque.
- **Privacidad total** — la ubicación nunca sale del dispositivo. Sin telemetría, sin analytics, sin servidores propios.

---

## Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin (compilador K2) | 2.2 |
| UI | Jetpack Compose + Material 3 | BOM 2026.02 |
| Navegación | Compose Navigation (type-safe) | 2.8.5 |
| Inyección de dependencias | Koin | 4.0.0 |
| Async | Kotlin Coroutines + Flow | 1.9.0 |
| Serialización | kotlinx.serialization | 1.7.3 |
| Fecha / hora | kotlinx-datetime | 0.6.1 |
| Persistencia | DataStore Preferences | 1.1.1 |
| Mapa | MapLibre Android SDK | 11.13.0 |
| Ubicación | Google Play Services — Fused Location Provider | 21.3.0 |
| Imágenes | Coil | 3.0.4 |
| Logging | Timber (solo debug) | 5.0.1 |
| Build | Gradle Kotlin DSL + Version Catalog | AGP 9.1.1 |
| Minificación | R8 (release) | — |
| JDK | Java 17 | — |

---

## Arquitectura

CVP sigue **Clean Architecture** estricta en tres capas, con dependencias unidireccionales:

```
┌─────────────┐        ┌────────────┐        ┌──────────────────┐
│    data     │───────▶│   domain   │◀───────│  presentation    │
│             │        │            │        │                  │
│ Repositorios│        │  Modelos   │        │  ViewModels      │
│ DTOs/Mapper │        │  UseCases  │        │  Composables     │
│ LocationSvc │        │  Interfaces│        │  UI State        │
│ DataStore   │        │  Utils     │        │                  │
└─────────────┘        └────────────┘        └──────────────────┘
```

- **`domain`** — Lógica de negocio pura. Cero dependencias de Android.
- **`data`** — Implementaciones de repositorios, carga de GeoJSON, servicio de ubicación.
- **`presentation`** — ViewModels y pantallas en Compose. Solo habla con `domain`.
- **`di/`** — Módulos de Koin que conectan las tres capas.

### Decisiones de diseño destacadas

| Componente | Decisión |
|---|---|
| **Índice espacial** | Geohash propio (sin librerías externas) para lookup O(1) en ~5 000 zonas |
| **Distancia** | Fórmula de Haversine para distancias precisas sobre la esfera terrestre |
| **Deduplicación** | Set en memoria por sesión — cada zona alerta solo una vez |
| **Severidad** | Calculada en el mapper: HIGH si hay víctimas fatales o ≥ 3 graves |
| **Alertas direccionales** | Bearing delta entre posición del usuario y zona; ignora zonas en el cono trasero |

---

## Requisitos previos

- Android Studio **Meerkat** o superior
- **JDK 17**
- Una cuenta en [mapbox.com](https://account.mapbox.com/) para los tokens de mapa

---

## Configurar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/ADRIELCELSOROSALES/MemoriaVial-Android.git
cd MemoriaVial-Android
```

### 2. Crear `local.properties`

```bash
cp local.properties.template local.properties
```

Editá el archivo con tus valores:

```properties
sdk.dir=/Users/<tu-usuario>/Library/Android/sdk

# Token para descargar el SDK de Mapbox desde Maven (scope: DOWNLOADS:READ)
MAPBOX_DOWNLOADS_TOKEN=sk.eyJ...

# Token público para estilos y tiles (scopes: styles:read, tiles:read, fonts:read)
MAPBOX_ACCESS_TOKEN=pk.eyJ...
```

Para builds de release, agregá también las credenciales del keystore (ver [Firmar el APK](#firmar-el-apk)).

### 3. Agregar el dataset de zonas

Copiá el archivo `risk_zones.geojson` (generado desde el pipeline de datos) en:

```
app/src/main/assets/risk_zones.geojson
```

Este archivo **no está en el repositorio** porque es un asset regenerable. El proyecto compila sin él usando el dataset de muestra mínimo incluido. Para producción usá el dataset completo del Observatorio.

**Formato esperado:** GeoJSON `FeatureCollection` de puntos (`Point`). Propiedades por feature:

| Campo | Tipo | Default si ausente |
|---|---|---|
| `id` | string | generado |
| `incident_count` | int | 0 |
| `leve_count` | int | 0 |
| `grave_count` | int | 0 |
| `mortal_count` | int | 0 |
| `predominant_hour_range` | string / null | `"NA"` |
| `predominant_weekday` | string / null | `"NA"` |
| `via_type` | string / null | `"NA"` |
| `predominant_victim_mode` | string / null | `"NA"` |
| `address_label` | string / null | `"NA"` |
| `comuna` | int / null | 0 |
| `radius_meters` | float / null | 25 m |

---

## Compilar

### Debug

```bash
# Solo compilar
./gradlew :app:assembleDebug

# Compilar e instalar en dispositivo/emulador conectado
./gradlew :app:installDebug
```

O usá **Run ▶** en Android Studio.

### Release

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

3. Compilá:

```bash
# APK firmado
./gradlew :app:assembleRelease

# Android App Bundle (para Google Play)
./gradlew :app:bundleRelease
```

Los artefactos se generan en `app/build/outputs/`.

---

## Tests

### Unitarios (sin dispositivo)

```bash
./gradlew test
```

Cobertura: `GeohashTest`, `GeohashIndexTest`, `DistanceUtilsTest`, `DetectNearbyZonesUseCaseTest`, `ShouldNotifyUseCaseTest`, `SettingsRepositoryTest`, `GeoJsonLoaderTest`, `RiskZoneMapperTest`, `RiskZoneRepositoryTest`, `SeverityTest`.

### Instrumentación (requiere dispositivo o emulador)

```bash
./gradlew connectedAndroidTest
```

Cobertura: `MapScreenTest`, `OnboardingScreenTest`, `ZoneDetailBottomSheetTest`.

### Lint

```bash
./gradlew lint
```

### Todo junto (recomendado antes de push)

```bash
./gradlew test lint
```

---

## Estructura de paquetes

```
com.cvp.app
├── core/design
│   ├── animations/      # Duraciones y curvas de animación
│   ├── components/      # CvpButton, CvpCard, CvpChip, CvpEmptyState…
│   └── theme/           # CvpTheme, colores, tipografía (Inter), espaciado, formas
├── data
│   ├── local
│   │   ├── dto/         # DTOs GeoJSON para kotlinx.serialization
│   │   ├── mapper/      # RiskZoneMapper: DTO → modelo de dominio
│   │   └── preferences/ # OnboardingPreferences (DataStore)
│   ├── location/        # LocationService: Fused Location → Flow<UserLocation>
│   └── repository/      # RiskZoneRepositoryImpl, SettingsRepositoryImpl
├── di/                  # Módulos Koin
├── domain
│   ├── model/           # RiskZone, Severity, UserLocation, ProximityEvent, AppSettings
│   ├── repository/      # Interfaces (sin dependencias Android)
│   ├── usecase/         # DetectNearbyZonesUseCase, ShouldNotifyUseCase
│   └── util/            # DistanceUtils (Haversine), Geohash, GeohashIndex
└── presentation
    ├── common/          # LocationPermissionHandler
    ├── map/             # MapScreen, MapViewModel, CvpMapState, RiskZoneRenderer
    ├── onboarding/      # OnboardingScreen, OnboardingViewModel
    ├── settings/        # SettingsScreen, SettingsViewModel, AboutScreen, PrivacyScreen
    ├── welcome/         # WelcomeScreen
    └── zonedetail/      # ZoneDetailBottomSheet
```

---

## Manejo de errores

| Escenario | Comportamiento |
|---|---|
| GeoJSON corrupto o faltante | `MapScreen` muestra `CvpEmptyState` con botón "Reintentar" |
| Permiso de ubicación denegado | Card flotante invitando a reactivarlo; el mapa sigue funcionando |
| Permiso revocado en runtime | App operativa sin posición del usuario |
| Error transitorio de ubicación | Snackbar con mensaje descriptivo |

---

## Roadmap

- [ ] **Filtros por severidad** — toggle en el mapa para mostrar solo zonas HIGH / MEDIUM / LOW.
- [ ] **Modo navegación** — alertas de audio al acercarse a zonas mientras se conduce.
- [ ] **Notificaciones en segundo plano** — `ForegroundService` para alertas con pantalla apagada.
- [ ] **Dataset actualizable** — descarga incremental desde CDN en lugar de asset estático.
- [ ] **Clusters** — agrupación visual de zonas cercanas a zoom bajo.
- [ ] **Modo offline total** — cacheo de tiles del mapa para uso sin conexión.
- [ ] **iOS** — reimplementación en SwiftUI con lógica de dominio compartida vía KMP.

---

## Privacidad

CVP no recopila, almacena ni transmite datos personales.
La ubicación del usuario **nunca sale del dispositivo**.
Ver la [política de privacidad completa](docs/privacy.html).

---

## Contribuir

Ver [CONTRIBUTING.md](CONTRIBUTING.md).

---

## Licencia

[MIT License](LICENSE.md) © 2025 Adriel Celso Rosales
