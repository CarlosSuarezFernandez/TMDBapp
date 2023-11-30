package com.carlosdev.tmdbapp.core

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    (activity as? AppCompatActivity)?.hideKeyboard()
}