import 'package:equatable/equatable.dart';

class $nameState extends Equatable {
  final String name;

  const $nameState({required this.name});

  $nameState copyWith({
    String? name,
  }) {
    return $nameState(
      name: name ?? this.name,
    );
  }

  @override
  List<Object> get props => [name];
}
