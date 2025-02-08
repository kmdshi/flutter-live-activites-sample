// ignore_for_file: public_member_api_docs, sort_constructors_first
class LiveActivityModel {
  int stage;
  int minutesToDelivery;

  LiveActivityModel({
    required this.stage,
    required this.minutesToDelivery,
  });

  factory LiveActivityModel.fromJson(Map<String, dynamic> json) {
    return LiveActivityModel(
      stage: json['stage'] as int,
      minutesToDelivery: json['minutesToDelivery'] as int,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'stage': stage,
      'minutesToDelivery': minutesToDelivery,
    };
  }
}
