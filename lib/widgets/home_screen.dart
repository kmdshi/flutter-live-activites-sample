import 'dart:async';

import 'package:flutter/material.dart';
import 'package:live_acticity_article/model/live_activity_model.dart';
import 'package:live_acticity_article/service/live_activity_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  LiveActivityAndroidService liveActivityService = LiveActivityAndroidService();

  LiveActivityModel liveActivityModel =
      LiveActivityModel(stage: 1, minutesToDelivery: 10, stagesCount: 4);

  Timer? timer;

  @override
  void dispose() {
    endNotifications();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: const Text("Live activity in Android"),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Center(
            child: ElevatedButton(
              onPressed: () async {
                setState(() {
                  liveActivityModel = LiveActivityModel(
                      stage: 1, minutesToDelivery: 10, stagesCount: 4);
                });
                await liveActivityService
                    .startNotifications(data: liveActivityModel)
                    .then((value) {
                  startNotifications();
                });
              },
              child: const Text("Start Notifications"),
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: () {
              endNotifications();
            },
            child: const Text("End Notifications"),
          )
        ],
      ),
    );
  }

  void startNotifications() {
    timer?.cancel();

    timer = Timer.periodic(const Duration(seconds: 10), (value) async {
      liveActivityModel.stage += 1;
      liveActivityModel.minutesToDelivery -= 3;

      if (liveActivityModel.stage == 2 || liveActivityModel.stage == 3) {
        await liveActivityService.updateNotifications(data: liveActivityModel);
      }

      if (liveActivityModel.stage == 4) {
        await liveActivityService
            .finishNotifications(data: liveActivityModel)
            .then((value) {
          timer?.cancel();
        });
      }
    });
  }

  void endNotifications() {
    timer?.cancel();
    setState(() {
      liveActivityModel =
          LiveActivityModel(stage: 1, minutesToDelivery: 10, stagesCount: 4);
    });
    liveActivityService.endNotifications(data: liveActivityModel);
  }
}
