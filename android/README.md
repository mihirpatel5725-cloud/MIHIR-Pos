# RK Billing Android APK

આ ફોલ્ડર Android Studio માટેનું Kotlin APK project છે. તે હાલના RK Billing UI અને JavaScriptને APKની અંદર offline assets તરીકે pack કરે છે. Logo અને UPI QR upload Android file pickerથી કામ કરે છે અને data LocalStorageમાં રહે છે.

## Android Studioથી APK બનાવો

1. આ repository download/clone કરો.
2. Android Studioમાં માત્ર `android` folder ખોલો.
3. Gradle sync પૂરો થવા દો.
4. **Build → Build APK(s)** પસંદ કરો.
5. Debug APK અહીં મળશે:

`android/app/build/outputs/apk/debug/app-debug.apk`

ફોનમાં APK મોકલીને install કરો. અજ્ઞાત source માટે Android Settingsમાં permission આપવી પડી શકે છે.

## Command line

Android SDK અને Gradle ઉપલબ્ધ હોય તો:

```bash
cd android
./gradlew assembleDebug
```

આ repositoryમાં wrapper JAR સામેલ નથી; Android Studioનું bundled Gradle અથવા Android Studio દ્વારા generated wrapper વાપરો.

## Printer

APK Androidનું native system print dialog ખોલે છે. Billingમાં **Save & Print**, Ordersમાં **Reprint**, અને Printer Settingsમાં **Print test** વાપરો. Bluetooth printerને Android Bluetooth Settingsમાં pair કરો અથવા USB printer માટે USB OTG વાપરો. 58mm/80mm અને logo/QR controls receipt preview/print CSSમાં લાગુ પડે છે.

સીધી raw ESC/POS Bluetooth bytes માટે આગળના native printer driverમાં તમારા printer modelનું SDK/command profile ઉમેરવું પડશે; આ build browser/system print service સાથે સુરક્ષિત રીતે કામ કરે છે.

## મહત્વપૂર્ણ

- APK build પહેલાં rootના `index.html`, `style.css`, `script.js`, `manifest.json` અને `service-worker.js` જરૂરી છે.
- Gradle build દરેક વખતે root web filesને `android/app/src/main/assets`માં copy કરે છે.
- App internet વગર ચાલે છે; internet permission માત્ર optional browser resources માટે છે.
- Signed release માટે Android Studioમાં **Build → Generate Signed Bundle/APK** પસંદ કરો.
