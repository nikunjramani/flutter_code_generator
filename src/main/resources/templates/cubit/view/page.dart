import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../cubit/cubit.dart';
import 'view.dart';

class $namePage extends StatelessWidget {
  const $namePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(create: (BuildContext context) => $nameCubit(), child: const $nameView());
  }
}
