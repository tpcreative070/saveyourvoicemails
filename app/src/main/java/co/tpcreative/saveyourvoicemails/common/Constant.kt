package co.tpcreative.saveyourvoicemails.common


class Constant {
    class ACTION {
        companion object {
            const val START_SERVICE = "START_SERVICE"
            const val START_RECORDING = "START_RECORDING"
            const val PAUSE_RECORD = "PAUSE_RECORD"
            const val RESUME_RECORD = "RESUME_RECORD"
            const val EXIT_APP_RECORD = "EXIT_APP_RECORD"
            const val SETTING = "SETTING"
            const val START_HOME = "START_HOME"
            const val START_SETTING = "START_SETTING"
            const val GO_HOME = "GO_HOME"
            const val GO_SETTING = "GO_SETTING"
            const val FILE_PATH = "FILE_PATH"
            const val TOOLS = "TOOLS"
            const val REQUEST_PERMISSION_CAM = "REQUEST_PERMISSION_CAM"
        }
    }

    companion object {
        const val NAME_NOTIFICATION = "NOTIFICATION"
        const val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
        const val ID_NOTIFICATION_FOREGROUND_SERVICE = 8466503
    }
}
