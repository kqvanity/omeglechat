package com.chatter.omeglechat.ChatScreen

import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Scroll to the bottom of a lazy list. It can be conveniently used when in conversation with each new message in the current user view.
 *
 * @param scrollState The state of the lazy list, upon which this method will work on.
 * @param coroutineScope the coroutine scope that's used to run the animateScrollToItem method.
 */
internal fun scrollToBottom(scrollState: LazyListState, coroutineScope: CoroutineScope) { // nap
    /*
        - I guess it needs a bit of refinement.
            - Like what if the user explicitly scrolled up.
            - Should i force him down with each new message, or maybe i should notify him of a new incoming message like whatsapp does?
     */
    coroutineScope.launch {     // nap
        val lazyColumnItemsCount = scrollState.layoutInfo.totalItemsCount   // nap
        if (lazyColumnItemsCount > 0) {
            scrollState.animateScrollToItem(lazyColumnItemsCount - 1)     // Scroll to the last item. Still not working fully
        }
    }
}

