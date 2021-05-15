package wear.weather.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import wear.weather.R
import wear.weather.view.fragment.ImageTaggerFragment

class ImageTagActivity : FragmentActivity() {
    var taggerFragment: ImageTaggerFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

//		---- Simple setup ----
//		ImageTaggerFragment taggerFragment = new ImageTaggerFragment(R.layout.fragment_tagger);

//		---- Define your own set of animations ----
//		ImageTaggerFragment taggerFragment = new ImageTaggerFragment(R.layout.fragment_tagger,
//																																	R.anim.zoom_in,
//																																	R.anim.zoom_out,
//																																	R.anim.zoom_large,
//																																	R.anim.zoom_normal);

//	---- Custom tag event handling ----
        taggerFragment = ImageTaggerFragment.newInstance(R.layout.fragment_tagger)
        //		{
//			@Override
//			public boolean onTagEvent(TagFragment tag, String tagEvent, Object data) {
//				return super.onTagEvent(tag, tagEvent, data);
//			}
//		};
        getSupportFragmentManager().beginTransaction()
            .add(R.id.fragment_container, taggerFragment!!, "imageTaggerFragment")
            .commit()
    }
}