class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                val config = ApiClient.configService.getAppConfig()
                val minVersionCode = config.minSupportedVersion.filter { it.isDigit() }.toIntOrNull()
                if (minVersionCode != null && BuildConfig.VERSION_CODE < minVersionCode) {
                    val appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity)
                    appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                        if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                            appUpdateManager.startUpdateFlow(
                                info,
                                this@MainActivity,
                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Ignore errors
            }
        }

        val startDestination = Rutes.Splash

        setContent {
            val navController = rememberNavController()
            val repo = remember { AlbaraPendingRepository(applicationContext) }

            RootScaffold(navController = navController, repo = repo)

            ValidacioFacturesAlbaransTheme {
                val navController = rememberNavController()
                val repo = remember { AlbaraPendingRepository(applicationContext) }

                AppNavGraph(
                    navController = navController,
                    repo = repo,
                    startDestination = startDestination
                )
            }
        }
    }
}
