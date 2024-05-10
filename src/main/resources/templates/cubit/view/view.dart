import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../cubit/cubit.dart';
import '../cubit/state.dart';

class $nameView extends StatelessWidget {
  const $nameView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: BlocListener<$nameCubit, $nameState>(
          listener: (context, state) {
            // TODO: implement listener}
          },
          child: Column(
            children: [
              BlocBuilder<$nameCubit, $nameState>(
                builder: (context, state) {
                  return Container();
                },
              ),
              BlocSelector<$nameCubit, $nameState, dynamic>(
                // TODO: change dynamic type
                selector: (state) {
                  // TODO: return selected state based on the provided state.
                  return state.name;
                },
                builder: (context, state) {
                  // TODO: return widget here based on the selected state.
                  return Text(state.toString());
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}
