import 'package:flutter_bloc/flutter_bloc.dart';

import 'event.dart';
import 'state.dart';

class $nameBloc extends Bloc<$nameEvent, $nameState> {
  $nameBloc() : super(const $nameState(name: "")) {
    on<InitEvent>(_init);
  }

  void _init(InitEvent event, Emitter<$nameState> emit) async {
    emit(state.copyWith(name: "init"));
  }
}
