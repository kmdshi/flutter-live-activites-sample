import 'package:flutter/services.dart';
import 'package:live_acticity_article/model/live_activity_model.dart';

class LiveActivityAndroidService {
  final MethodChannel _method =
      const MethodChannel("flutterAndroidLiveActivity");

  Future<void> startNotifications({required LiveActivityModel data}) async {
    try {
      await _method.invokeMethod("startDelivery", data.toJson());
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }

  Future<void> updateNotifications({required LiveActivityModel data}) async {
    try {
      await _method.invokeMethod("updateDeliveryStatus", data.toJson());
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }

  Future<void> finishNotifications({required LiveActivityModel data}) async {
    try {
      await _method.invokeMethod("finishDelivery", data.toJson());
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }

  Future<void> endNotifications({required LiveActivityModel data}) async {
    try {
      await _method.invokeMethod("endNotifications");
    } on PlatformException catch (e) {
      throw PlatformException(code: e.code);
    }
  }
}
