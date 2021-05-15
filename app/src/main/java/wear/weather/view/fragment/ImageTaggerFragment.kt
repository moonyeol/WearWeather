package wear.weather.view.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import wear.weather.R
import wear.weather.handler.TagCallbackHandler
import java.util.*

class ImageTaggerFragment : Fragment(), TagCallbackHandler, OnTouchListener {
    var selectedTagFragment: TagFragment? = null
    var tagContainer = R.layout.fragment_tagger
    var tagEnterAnimation = R.anim.zoom_in
    var tagExitAnimation = R.anim.zoom_out
    var tagSelectedAnimation = R.anim.zoom_large
    var tagDeselectedAnimation = R.anim.zoom_normal
    var mTagWidth = 142
    var mTagHeight = 142
    var mTagFragmentList: MutableList<TagFragment> =
        ArrayList<TagFragment>()
    var mainImage: ImageView? = null
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(tagContainer, container, false)
        mainImage = v.findViewById<View>(R.id.image_view) as ImageView
        v.setOnTouchListener(this)
        for (t in mTagFragmentList) {
            t.handler = this
        }
        v.bringToFront()
        mainImage!!.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            for (tag in mTagFragmentList) {
                var x: Double
                var y: Double
                x = tag.percentWidth * mainImage!!.width
                y = tag.percentHeight * mainImage!!.height
                moveTagTo(tag, x.toInt(), y.toInt(), true)
                tag.getView()?.measure(2 * mTagWidth, 2 * mTagHeight)
            }
        }
        return v
    }

    fun removeTag(tag: TagFragment) {
        mTagFragmentList.remove(tag)
        getFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out)
            ?.remove(tag)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun deselectTag(tag: TagFragment) {
        tag.setSelected(false)
        selectedTagFragment = null
        val zoomToLargeAnimation = AnimationUtils.loadAnimation(
            getActivity()?.getApplicationContext(),
            tagDeselectedAnimation
        )
        tag.getView()?.startAnimation(zoomToLargeAnimation)
    }

    fun selectTag(tag: TagFragment) {
        for (t in mTagFragmentList) t.setSelected(false)
        val zoomToLargeAnimation = AnimationUtils.loadAnimation(
            getActivity()?.getApplicationContext(),
            tagSelectedAnimation
        )
        tag.getView()?.startAnimation(zoomToLargeAnimation)
        tag.getView()?.bringToFront()
        selectedTagFragment = tag
        tag.setSelected(true)
    }

    fun addTagFragment(tag: TagFragment, x: Int, y: Int) {
        tag.handler = this
        getFragmentManager()
            ?.beginTransaction()
            ?.setCustomAnimations(tagEnterAnimation, tagExitAnimation)
            ?.add(R.id.tagger_fragment_container, tag)
            ?.addToBackStack(null)
            ?.commit()
        mTagFragmentList.add(tag)
        moveTagTo(tag, x, y, false)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun moveTagTo(
        tag: TagFragment,
        x: Int,
        y: Int,
        skipRecalcOfPercentages: Boolean
    ) {
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(x - mTagWidth, y - mTagHeight, 0, 0)
        lp.layoutDirection = 1
        tag.position = lp
        if (!skipRecalcOfPercentages) {
            val percentOfWidthFromLeft = x.toDouble() / mainImage!!.width.toDouble()
            val percentOfHeightFromTop = y.toDouble() / mainImage!!.height.toDouble()
            tag.percentWidth = percentOfWidthFromLeft
            tag.percentHeight = percentOfHeightFromTop
        }
        if (tag.getView() != null) {
            tag.getView()!!.setLayoutParams(lp)
            tag.getView()!!.bringToFront()
            getView()?.invalidate()
        }
    }

    override fun onTagEvent(tag: TagFragment?, eventName: String?, data: Any?): Boolean {
        if (eventName == "CloseClicked") {
            if (tag != null) {
                removeTag(tag)
            }
        } else if (eventName == "ClickDown") {
            if (tag != null) {
                selectTag(tag)
            }
        } else if (eventName == "ClickUp") {
            if (tag != null) {
                deselectTag(tag)
            }
        }
        return true
    }

    override fun onTagCreated(tag: TagFragment?) {
        for (t in mTagFragmentList) if (t !== tag) t.setSelected(false)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> if (selectedTagFragment != null) {
                moveTagTo(selectedTagFragment!!, event.x.toInt(), event.y.toInt(), false)
            }
            MotionEvent.ACTION_UP -> if (selectedTagFragment != null) {
                val zoomToNormalAnimation = AnimationUtils.loadAnimation(
                    getActivity()?.getApplicationContext(),
                    tagDeselectedAnimation
                )
                selectedTagFragment!!.view!!.startAnimation(zoomToNormalAnimation)
                selectedTagFragment = null
            } else {
                val tag: TagFragment =
                    TagFragment.newInstance(this, null)
                addTagFragment(tag, event.x.toInt(), event.y.toInt())
            }
        }
        return true
    }

    companion object {
        fun newInstance(tagContainer: Int): ImageTaggerFragment {
            val taggerFragment: ImageTaggerFragment = ImageTaggerFragment()
            taggerFragment.tagContainer = tagContainer
            return taggerFragment
        }

        fun newInstance(
            tagContainer: Int,
            tagEnterAnimation: Int,
            tagExitAnimation: Int
        ): ImageTaggerFragment {
            val taggerFragment: ImageTaggerFragment = ImageTaggerFragment()
            taggerFragment.tagContainer = tagContainer
            taggerFragment.tagExitAnimation = tagEnterAnimation
            taggerFragment.tagExitAnimation = tagExitAnimation
            return taggerFragment
        }

        fun newInstance(
            tagContainer: Int,
            tagEnterAnimation: Int,
            tagExitAnimation: Int,
            tagSelectedAnimation: Int,
            tagDeselectedAnimation: Int
        ): ImageTaggerFragment {
            val taggerFragment: ImageTaggerFragment = ImageTaggerFragment()
            taggerFragment.tagContainer = tagContainer
            taggerFragment.tagEnterAnimation = tagEnterAnimation
            taggerFragment.tagExitAnimation = tagExitAnimation
            taggerFragment.tagSelectedAnimation = tagSelectedAnimation
            taggerFragment.tagDeselectedAnimation = tagDeselectedAnimation
            return taggerFragment
        }
    }

    init {
        selectedTagFragment = null
        tagContainer = R.layout.fragment_tagger
        tagEnterAnimation = R.anim.zoom_in
        tagExitAnimation = R.anim.zoom_out
        tagSelectedAnimation = R.anim.zoom_large
        tagDeselectedAnimation = R.anim.zoom_normal
        mTagHeight = 140
        mTagWidth = mTagHeight
    }
}
