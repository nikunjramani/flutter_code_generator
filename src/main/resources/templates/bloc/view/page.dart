import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../bloc/blocs.dart';
import '../bloc/event.dart';
import 'view.dart';

class $namePage extends StatelessWidget {
  const $namePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(create: (BuildContext context) => $nameBloc(), child: const $nameView());

  }
}
