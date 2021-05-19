package uz.creater.pdpgramm.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uz.creater.pdpgramm.R
import uz.creater.pdpgramm.adapters.ViewPagerAdapter
import uz.creater.pdpgramm.databinding.FragmentMainBinding
import uz.creater.pdpgramm.utils.ZoomOutPageTransformer

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(listOf(1, 2), requireActivity())
        binding.viewpager.adapter = viewPagerAdapter
        binding.viewpager.setPageTransformer(ZoomOutPageTransformer())
        binding.chatsButton.tag = "selected"
        binding.groupsButton.tag = "unselected"
        binding.chatsButton.setOnClickListener {
            if (binding.chatsButton.tag == "unselected") {
                binding.chatsButton.setBackgroundResource(R.drawable.selected_button_back)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context?.getColor(R.color.white)?.let { it1 ->
                        binding.chatsButton.setTextColor(
                            it1
                        )
                    }
                    context?.getColor(R.color.unselected_text_color)?.let { it1 ->
                        binding.groupsButton.setTextColor(
                            it1
                        )
                    }
                } else {
                    //noinspection deprecation
                    context?.resources?.getColor(R.color.white)?.let { it1 ->
                        binding.chatsButton.setTextColor(
                            it1
                        )
                    }
                    context?.resources?.getColor(R.color.unselected_text_color)?.let { it1 ->
                        binding.groupsButton.setTextColor(
                            it1
                        )
                    }
                }
                binding.groupsButton.setBackgroundResource(R.drawable.unselected_button_back)
                binding.chatsButton.tag = "selected"
                binding.groupsButton.tag = "unselected"
                binding.viewpager.currentItem = 0
            }
        }

        binding.groupsButton.setOnClickListener {
            if (binding.groupsButton.tag == "unselected") {
                binding.groupsButton.setBackgroundResource(R.drawable.selected_button_back)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context?.getColor(R.color.white)?.let { it1 ->
                        binding.groupsButton.setTextColor(
                            it1
                        )
                    }
                    context?.getColor(R.color.unselected_text_color)?.let { it1 ->
                        binding.chatsButton.setTextColor(
                            it1
                        )
                    }
                } else {
                    //noinspection deprecation
                    context?.resources?.getColor(R.color.white)?.let { it1 ->
                        binding.groupsButton.setTextColor(
                            it1
                        )
                    }
                    context?.resources?.getColor(R.color.unselected_text_color)?.let { it1 ->
                        binding.chatsButton.setTextColor(
                            it1
                        )
                    }
                }
                binding.chatsButton.setBackgroundResource(R.drawable.unselected_button_back)
                binding.chatsButton.tag = "unselected"
                binding.groupsButton.tag = "selected"
                binding.viewpager.currentItem = 1
            }
        }
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    if (binding.chatsButton.tag == "unselected") {
                        binding.chatsButton.setBackgroundResource(R.drawable.selected_button_back)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            context?.getColor(R.color.white)?.let { it1 ->
                                binding.chatsButton.setTextColor(
                                    it1
                                )
                            }
                            context?.getColor(R.color.unselected_text_color)?.let { it1 ->
                                binding.groupsButton.setTextColor(
                                    it1
                                )
                            }
                        } else {
                            //noinspection deprecation
                            context?.resources?.getColor(R.color.white)?.let { it1 ->
                                binding.chatsButton.setTextColor(
                                    it1
                                )
                            }
                            context?.resources?.getColor(R.color.unselected_text_color)
                                ?.let { it1 ->
                                    binding.groupsButton.setTextColor(
                                        it1
                                    )
                                }
                        }
                        binding.groupsButton.setBackgroundResource(R.drawable.unselected_button_back)
                        binding.chatsButton.tag = "selected"
                        binding.groupsButton.tag = "unselected"
                    }
                } else {
                    if (binding.groupsButton.tag == "unselected") {
                        binding.groupsButton.setBackgroundResource(R.drawable.selected_button_back)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            context?.getColor(R.color.white)?.let { it1 ->
                                binding.groupsButton.setTextColor(
                                    it1
                                )
                            }
                            context?.getColor(R.color.unselected_text_color)?.let { it1 ->
                                binding.chatsButton.setTextColor(
                                    it1
                                )
                            }
                        } else {
                            //noinspection deprecation
                            context?.resources?.getColor(R.color.white)?.let { it1 ->
                                binding.groupsButton.setTextColor(
                                    it1
                                )
                            }
                            context?.resources?.getColor(R.color.unselected_text_color)
                                ?.let { it1 ->
                                    binding.chatsButton.setTextColor(
                                        it1
                                    )
                                }
                        }
                        binding.chatsButton.setBackgroundResource(R.drawable.unselected_button_back)
                        binding.chatsButton.tag = "unselected"
                        binding.groupsButton.tag = "selected"
                    }
                }
                super.onPageSelected(position)
            }
        })
        return binding.root
    }
}