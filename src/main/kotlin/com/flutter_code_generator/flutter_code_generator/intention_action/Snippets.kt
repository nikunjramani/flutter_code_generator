package com.flutter_code_generator.flutter_code_generator.intention_action

object Snippets {
    const val PREFIX_SELECTION: String = "Subject"

    private const val SUFFIX1: String = "Bloc"
    private const val SUFFIX2: String = "State"
    private const val SUFFIX3: String = "Repository"

    const val BLOC_SNIPPET_KEY: String = PREFIX_SELECTION + SUFFIX1
    const val STATE_SNIPPET_KEY: String = PREFIX_SELECTION + SUFFIX2
    const val REPOSITORY_SNIPPET_KEY: String = PREFIX_SELECTION + SUFFIX3

    fun getSnippet(snippetType: SnippetType?, widget: String): String {
        return when (snippetType) {
            SnippetType.BlocListener -> blocListenerSnippet(
                widget
            )

            SnippetType.BlocProvider -> blocProviderSnippet(
                widget
            )

            SnippetType.BlocConsumer -> blocConsumerSnippet(
                widget
            )

            SnippetType.RepositoryProvider -> repositoryProviderSnippet(
                widget
            )

            else -> blocBuilderSnippet(widget)
        }
    }

    private fun blocBuilderSnippet(widget: String): String {
        return String.format(
            """BlocBuilder<%1${"$"}s, %2${"$"}s>(
  builder: (context, state) {
    return %3${"$"}s;
  },
)""", BLOC_SNIPPET_KEY, STATE_SNIPPET_KEY, widget
        )
    }

    private fun blocListenerSnippet(widget: String): String {
        return String.format(
            """BlocListener<%1${"$"}s, %2${"$"}s>(
  listener: (context, state) {
    // TODO: implement listener}
  },
  child: %3${"$"}s,
)""", BLOC_SNIPPET_KEY, STATE_SNIPPET_KEY, widget
        )
    }

    private fun blocProviderSnippet(widget: String): String {
        return String.format(
            """BlocProvider(
  create: (context) => %1${"$"}s(),
  child: %2${"$"}s,
)""", BLOC_SNIPPET_KEY, widget
        )
    }

    private fun blocConsumerSnippet(widget: String): String {
        return String.format(
            """BlocConsumer<%1${"$"}s, %2${"$"}s>(
  listener: (context, state) {
    // TODO: implement listener
  },
  builder: (context, state) {
    return %3${"$"}s;
  },
)""", BLOC_SNIPPET_KEY, STATE_SNIPPET_KEY, widget
        )
    }

    private fun repositoryProviderSnippet(widget: String): String {
        return String.format(
            """RepositoryProvider(
  create: (context) => %1${"$"}s(),
    child: %2${"$"}s,
)""", REPOSITORY_SNIPPET_KEY, widget
        )
    }
}
