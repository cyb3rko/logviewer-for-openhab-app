## Upgrade Notes

### 2.0.0
- before installing the update uninstall the previous version and then installing the new version, because since now APK files will be signed; sometimes this circumstance leads to failing installations
- I've reset the app configuration so you have to add your hostname and port once again because I restructured the whole app configuration system and don't want to have unwanted app behaviour because of this
- the saving of your log text sizes for each orientation now only works seperately for AUTO, PORTRAIT and LANDSCAPE
    - which means that turning your device while having AUTO enabled won't change your text size anymore (SORRY, owed to the new app structure, maybe I will somehow manage to reimplement it)
