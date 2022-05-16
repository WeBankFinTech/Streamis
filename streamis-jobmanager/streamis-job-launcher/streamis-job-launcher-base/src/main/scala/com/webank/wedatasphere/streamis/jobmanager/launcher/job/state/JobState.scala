package com.webank.wedatasphere.streamis.jobmanager.launcher.job.state

trait JobState {

    def getLocation: String

    def metadataInfo: Any


}
