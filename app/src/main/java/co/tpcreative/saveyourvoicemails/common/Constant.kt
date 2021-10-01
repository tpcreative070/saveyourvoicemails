package co.tpcreative.saveyourvoicemails.common


class Constant {
    class ACTION {
        companion object {
            const val START_SERVICE = "START_SERVICE"
            const val START_RECORDING = "START_RECORDING"
            const val STOP_RECORDING = "STOP_RECORDING"
            const val START_RECORDING_PHONE_CALL = "START_RECORDING_PHONE_CALL"
            const val STOP_RECORDING_PHONE_CALL = "STOP_RECORDING_PHONE_CALL"
            const val EXIT_APP = "EXIT_APP"
            const val PAUSE_RECORD = "PAUSE_RECORD"
            const val RESUME_RECORD = "RESUME_RECORD"
            const val SETTING = "SETTING"
            const val START_HOME = "START_HOME"
            const val START_SETTING = "START_SETTING"
            const val GO_HOME = "GO_HOME"
            const val GO_SETTING = "GO_SETTING"
            const val FILE_PATH = "FILE_PATH"
            const val TOOLS = "TOOLS"
            const val REQUEST_PERMISSION_CAM = "REQUEST_PERMISSION_CAM"
            const val PHONE_NUMBER_TO_RECORD = "PHONE_NUMBER_TO_RECORD"
            const val CALL_RECORD_PATH = "CALL_RECORD_PATH"
            const val PHONE_CALL_NUMBER = "PHONE_CALL_NUMBER"
        }
    }

    companion object {
        const val NAME_NOTIFICATION = "NOTIFICATION"
        const val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
        const val FOREGROUND_CHANNEL_NAME = "foreground_channel_name"
        const val ID_NOTIFICATION_FOREGROUND_SERVICE = 8466503
        const val ONE_DAY = 86400000
        const val FIVE_DAY = ONE_DAY * 5
    }
}
