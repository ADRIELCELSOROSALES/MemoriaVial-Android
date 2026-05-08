# CVP Release Checklist

Pre-release checklist for Google Play submission. Complete every item before promoting a build from internal testing to production.

---

## 1. Version

- [ ] `versionCode` incremented in `app/build.gradle.kts` (must be strictly greater than the previously published value).
- [ ] `versionName` updated to the correct semantic version (e.g., `1.2.0`).
- [ ] Version values match across `build.gradle.kts` and any in-app "About" screen.

---

## 2. Tests

- [ ] All unit tests pass: `./gradlew test`
- [ ] All instrumentation tests pass: `./gradlew connectedAndroidTest`
- [ ] No new lint errors: `./gradlew lint`
- [ ] Lint report reviewed — no suppressed warnings left unexplained.

---

## 3. Release Build

- [ ] Release AAB generated: `./gradlew bundleRelease`
- [ ] AAB is signed with the **release keystore** (not the debug keystore).
- [ ] Signing is configured via `signingConfigs` in `build.gradle.kts` reading credentials from environment variables or a local properties file — keystore credentials are **never committed to the repository**.
- [ ] Keystore file location and credentials are documented securely (e.g., password manager, encrypted notes) and accessible to at least two trusted people.

---

## 4. ProGuard / R8

- [ ] `minifyEnabled = true` and `shrinkResources = true` are set for the `release` build type.
- [ ] The release build launches without crashes on a physical device.
- [ ] No `ClassNotFoundException` or `MethodNotFoundException` in logcat after ProGuard.
- [ ] Custom ProGuard rules in `proguard-rules.pro` are reviewed and up to date (especially for Koin, Mapbox, and kotlinx.serialization).

---

## 5. Logging

- [ ] `Timber.DebugTree` is **not** planted in the release build. Only plant `DebugTree` inside a `BuildConfig.DEBUG` check in `Application.onCreate()`.
- [ ] No sensitive data is logged at any level in the release build.

---

## 6. Privacy Policy

- [ ] Privacy policy HTML is published at its public URL (GitHub Pages or equivalent).
- [ ] URL is reachable from a browser with no login required.
- [ ] The URL in the Play Console "App content" section matches the published URL.
- [ ] Privacy policy URL is also present in the in-app "Privacy" screen (`PrivacyScreen`).

---

## 7. Play Store Listing

- [ ] Short description is 80 characters or under.
- [ ] Long description is proofread — no placeholder text, no broken formatting.
- [ ] At least 4 screenshots uploaded (phone form factor required; tablet optional).
- [ ] Screenshots accurately represent the current version of the app.
- [ ] App icon uploaded at **512 x 512 px** PNG.
- [ ] Feature graphic uploaded at **1024 x 500 px** PNG.
- [ ] All listing text is in Spanish (es-419 or es-ES) as the primary locale.

---

## 8. App Content

- [ ] Content rating questionnaire completed and rating confirmed as **Everyone**.
- [ ] Target audience set to general public (not directed at children).
- [ ] Data safety section completed:
  - Precise location: collected, not shared, used for app functionality, processed ephemerally (never sent to servers).
  - No other personal data collected.

---

## 9. Device Testing

- [ ] Tested on at least **3 physical devices** of different screen sizes (e.g., compact phone, large phone, tablet).
- [ ] Tested on **Android 8.0 / API 26** (minSdk) — verify no crashes and that all features work.
- [ ] Tested on the **latest available Android version**.
- [ ] Map renders correctly at multiple zoom levels on all tested devices.
- [ ] Proximity alerts trigger correctly on all tested devices.
- [ ] Dark mode and light mode both verified visually.
- [ ] Onboarding flow completes without errors on a fresh install (no previous `DataStore` data).

---

## 10. Post-Internal-Testing

- [ ] No crash logs in Play Console after the internal testing track.
- [ ] ANR rate is 0% on the internal track.
- [ ] All pre-launch report issues reviewed and addressed or explicitly accepted.

---

## 11. Data

- [ ] `risk_zones.geojson` (bundled asset) contains the latest dataset from the Observatorio de Movilidad y Seguridad Vial de CABA.
- [ ] GeoJSON parses without errors and all zone features render on the map.
- [ ] Dataset source date is noted in the release notes.

---

## 12. Permissions Justification

The following permissions are declared in `AndroidManifest.xml`. Have justification text ready for Play Store review if requested:

| Permission | Justification |
|---|---|
| `ACCESS_FINE_LOCATION` | Required to calculate the user's real-time distance from road risk zones and trigger proximity alerts. Processing is entirely on-device. |
| `POST_NOTIFICATIONS` | Required on Android 13+ to display proximity alert notifications. |
| `INTERNET` | Required to download map tile images from Mapbox servers. No user data is transmitted. |

---

## 13. Final Sign-off

- [ ] `CHANGELOG` or release notes drafted for this version.
- [ ] Build promoted from internal track to **closed testing** (or production) only after all items above are checked.
- [ ] Release reviewed by at least one other person before production rollout (if team size allows).
