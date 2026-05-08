# CVP — Google Play Store Listing Assets

This document defines the canonical copy and asset specifications for the Capa Vial Predictiva listing on Google Play.

---

## Category and Metadata

| Field | Value |
|---|---|
| App name | Capa Vial Predictiva |
| Developer name | Adriel Celso Rosales |
| Category | Maps & Navigation |
| Content rating | Everyone |
| Contact email | adrielrosales1999@gmail.com |
| Primary language | Spanish (es-419) |

**Keywords:** seguridad vial, Buenos Aires, siniestros, accidentes, mapa, zonas de riesgo, CABA, conduccion, alertas viales

---

## Slogan

> Conduca mas atento. CVP recuerda lo que vos olvidas.

---

## Short Description

80 characters maximum. Used in search results and the app tile.

```
Zonas con historial de siniestros viales en Buenos Aires.
```

Character count: 57 (within limit).

---

## Long Description

Target: 2000–3000 characters. Paste directly into the Play Console description field.

---

Capa Vial Predictiva (CVP) es una herramienta de seguridad vial para conductores y ciclistas en la Ciudad de Buenos Aires.

El conductor no es defectuoso. Solo es humano, y los seres humanos olvidan, se distraen, subestiman. CVP no juzga al conductor: le recuerda lo que los datos ya saben. Cada zona senalada en el mapa tiene detras un historial real de siniestros registrados por el Observatorio de Movilidad y Seguridad Vial de CABA. No es una prediccion del futuro. Es un registro honesto de donde el pasado se repitio.

**Que hace CVP:**

- Visualiza zonas con historial de siniestros viales directamente sobre el mapa de Buenos Aires. Cada zona esta categorizada por severidad (baja, media, alta) con una paleta de colores clara e intuitiva.

- Muestra datos oficiales del Observatorio de Movilidad y Seguridad Vial de la Ciudad de Buenos Aires. La informacion es publica, verificable y actualizada.

- Emite alertas de proximidad cuando te acercas a una zona con historial de siniestros. El radio de alerta es configurable. Todo el calculo ocurre en tu dispositivo: tu ubicacion nunca se transmite a ningun servidor.

- Incluye modo oscuro completo, alineado con el tema del sistema o configurable manualmente, para uso nocturno sin fatiga visual.

**Privacidad ante todo:**

CVP no crea cuentas de usuario, no recopila datos de uso, no muestra publicidad y no comparte ninguna informacion con terceros. La unica conexion de red que realiza la app es la descarga de tiles del mapa desde Mapbox. Tu ubicacion es tuya.

**Datos:**

Los datos de zonas de riesgo provienen del Observatorio de Movilidad y Seguridad Vial de la Ciudad Autonoma de Buenos Aires, organismo dependiente del Gobierno de la Ciudad. Son datos abiertos, de acceso publico.

**Aviso importante:**

CVP no predice el futuro ni garantiza la seguridad en ninguna zona. Mostrar donde el pasado se repitio no equivale a asegurar que volvera a ocurrir, ni que las zonas no marcadas son seguras. CVP es una herramienta de informacion, no un sistema de prevencion de accidentes. Siempre respeta las normas de transito y conduce con atencion independientemente de lo que muestre la aplicacion.

---

Descargala gratis. Sin cuentas. Sin tracking. Solo el mapa y los datos.

---

## Icon Specification

**File:** `ic_launcher_512.png`
**Size:** 512 x 512 px
**Format:** PNG, no transparency (Play Store requires opaque background)

**Composition:**

- Background: solid navy `#0F1B3C`.
- Foreground: white CVP map pin icon centered, occupying approximately 60% of the canvas height. The pin shape is a standard teardrop/location-pin silhouette with a circular cutout or small road-intersection graphic inside.
- No text on the icon — the icon must be recognizable at small sizes (48 px).
- No rounded corners applied by the designer — the Play Store applies the rounding mask automatically.

**Do not:**

- Use gradients that make the icon look washed out at small sizes.
- Add drop shadows to the foreground element.
- Include any text or the app name.

---

## Feature Graphic Specification

**File:** `feature_graphic_1024x500.png`
**Size:** 1024 x 500 px
**Format:** PNG

**Composition:**

- Background: horizontal gradient from `#0F1B3C` (left) to `#1A2F5E` (right), with a subtle map-grid or road-line texture at low opacity (10–15%) as a secondary layer.
- Left half: CVP app icon (same pin mark as above) at approximately 200 px, vertically centered, with sufficient padding from the left edge.
- Right half: two lines of text, vertically centered.
  - Line 1: "Capa Vial Predictiva" in Inter SemiBold, white, approximately 38 px.
  - Line 2 (slogan): "Conduca mas atento. CVP recuerda lo que vos olvidás." in Inter Regular, `rgba(255,255,255,0.70)`, approximately 22 px. Allow line wrapping if needed.
- No additional decorative elements that clutter the composition.

**Note:** The feature graphic may be cropped on some surfaces. Keep all critical content within the central 924 x 400 px safe zone (50 px margin on all sides).

---

## Screenshots

Minimum 4 screenshots required. Phone form factor (portrait). Recommended resolution: 1080 x 1920 px or 1440 x 2560 px. Format: PNG or JPEG.

### Screenshot 1 — Map with Risk Zones

**Filename:** `screenshot_01_map.png`

**Content:** The main map screen showing central Buenos Aires with multiple risk zone overlays visible. At least one high-severity zone (red/deep red), one medium zone (amber), and one low zone (teal/green) should be visible simultaneously. The user location dot is shown on the map. The top bar shows the "CVP" title. No bottom sheet is open.

**Composition note:** Center the map viewport on a recognizable Buenos Aires landmark area (e.g., Palermo, Microcentro) so users recognize the city immediately. Dark map style preferred (night basemap) to show the colored zones with maximum contrast.

---

### Screenshot 2 — Zone Detail Bottom Sheet

**Filename:** `screenshot_02_zone_detail.png`

**Content:** The main map screen with a bottom sheet expanded showing details of a selected risk zone. The sheet should show: zone severity badge (e.g., "ALTO"), number of registered incidents, the approximate intersection or street name, and a brief descriptive label. The map is partially visible behind the sheet.

**Composition note:** The bottom sheet should be expanded to approximately 50% of the screen height. Use a high-severity zone to make the severity badge visually prominent.

---

### Screenshot 3 — Settings Screen

**Filename:** `screenshot_03_settings.png`

**Content:** The settings screen showing all available options: theme selector (Sistema / Claro / Oscuro), alert radius slider or input (showing a value such as 300 m), and the notifications toggle. The top bar shows a back arrow and "Configuracion" as the title.

**Composition note:** Show the dark theme variant of this screen to demonstrate the dark mode capability. All controls should be in their default or a representative state.

---

### Screenshot 4 — Onboarding Welcome Screen

**Filename:** `screenshot_04_onboarding.png`

**Content:** The onboarding welcome screen shown on first launch. It should display the CVP logo/icon prominently, the app name "Capa Vial Predictiva", the slogan "Conduca mas atento. CVP recuerda lo que vos olvidás." and a primary call-to-action button to begin (e.g., "Comenzar" or "Ver el mapa"). The screen uses the navy background consistent with the brand.

**Composition note:** This screen serves as the brand introduction. The navy background, white typography, and centered layout should communicate the design system clearly. Avoid showing any permission dialogs in this screenshot.

---

## Localization Notes

- All listing copy is in Argentinian Spanish (`es-419`).
- Verb conjugations use voseo (e.g., "conduca", "olvidás", "vas").
- If a second locale is added in the future, `en-US` is the recommended fallback.
