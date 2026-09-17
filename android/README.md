# Daily Dashboard — Android-app

Kotlin/Jetpack Compose dashboard-app voor een Android-tablet: haalt data op bij de
[backend](../backend) en toont die in een vrij indeelbare widget-grid met vaste,
donkere visuele stijl.

## Belangrijkste afwijking van de skill-conventie

De `android-development`-skill (NowInAndroid-architectuur) gebruikt normaal Hilt voor
dependency injection. Deze build is opgezet in een cloud-sandbox zonder Android SDK,
dus kon hier niet gecompileerd worden — en Hilt/KSP-scoping is foutgevoelig om blind
te schrijven zonder compiler-feedback. Daarom is bewust gekozen voor **handmatige
dependency injection**: elke Repository en ViewModel heeft een simpele
`create(context)`/`factory(context)` companion-functie in plaats van `@Inject`/Hilt-modules.
Werkt identiek, maar zonder de KSP-annotatieverwerking van Hilt. Wil je alsnog Hilt,
dan is dat in Android Studio (met compiler-feedback) een relatief kleine refactor.

Room (database) en Compose gebruiken wel gewoon hun standaard KSP/compiler-plugins —
dat zijn veel mechanischere, voorspelbaardere patronen dan Hilt's DI-graph.

## Openen in Android Studio

1. Open de map `android/` als project in Android Studio (Ladybug of nieuwer).
2. Laat Gradle syncen — dit downloadt zelf de Android SDK-onderdelen, AGP, Compose BOM etc.
   (dat kon in deze cloud-sandbox niet, hier was geen toegang tot `dl.google.com`).
3. Vul in `core:network`'s backend-adres in via de instellingen-sheet in de app zelf
   (tandwiel-icoon rechtsboven), of pas de default in
   `core/datastore/src/main/kotlin/.../DashboardSettings.kt` aan.
4. Run op een tablet of emulator in hetzelfde wifi-netwerk als de backend-laptop.

## Modules

Zie de hoofdmap-`PROMPT.md` voor de volledige spec. Kort:

- `app` — navigatie-loze scaffolding, widget-grid, kiosk-modus (scherm blijft wakker), instellingen.
- `feature:agenda` / `feature:herinneringen` / `feature:systeem` / `feature:fs25` — elk een
  offline-first Repository + ViewModel + widget-content-composable.
- `core:network` — Retrofit-client met een dynamisch instelbaar backend-adres.
- `core:database` — Room, source of truth voor alle feature-data.
- `core:designsystem` — kleurtokens, typografie, basiscomponenten (1-op-1 uit de spec).
- `core:ui` — de vijf herbruikbare widget-kaarten (`StatCard`, `ChartCard`, `GaugeCard`,
  `CalloutCard`, `ListCard`), los van databron.
- `core:datastore` — widget-layout en instellingen (DataStore Preferences + JSON).

## Bekende beperkingen / nog te doen op je eigen machine

- **Niet gecompileerd**: deze sandbox had geen Android SDK-toegang, dus de code is
  geschreven volgens bekende, correcte patronen maar niet build-geverifieerd. Verwacht
  een eerste sync/build-ronde in Android Studio met mogelijk kleine fixes.
- **Lettertype**: gebruikt `FontFamily.SansSerif` als drop-in voor Inter. Voeg zelf
  Inter-`.ttf`-bestanden toe aan `core/designsystem/src/main/res/font` en wijzig
  `DashboardFontFamily` in `Type.kt` voor de exacte typografie uit de referentie.
- **Launcher-icoon**: verwijst nu naar een systeem-standaardicoon
  (`@android:drawable/sym_def_app_icon`). Vervang via Android Studio's Image Asset-tool.
- **Drag-and-drop**: zelfgeschreven (geen externe reorder-library), met
  `detectDragGesturesAfterLongPress` — werkt alleen in bewerkmodus (potlood-icoon rechtsboven).
