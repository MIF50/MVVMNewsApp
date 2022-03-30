package com.mif50.mvvmnewsapp.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

inline fun AppCompatActivity.fragmentByTag(tag: String): Fragment? {
    return this.supportFragmentManager.findFragmentByTag(tag)
}