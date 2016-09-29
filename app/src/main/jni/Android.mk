LOCAL_PATH:=$(callmy-dir)
 
include$(CLEAR_VARS)
 
LOCAL_MODULE:=FFT
LOCAL_SRC_FILES:=processRawData.c
 
include$(BUILD_SHARED_LIBRARY)