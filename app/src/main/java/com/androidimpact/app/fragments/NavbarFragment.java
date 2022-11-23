package com.androidimpact.app.fragments;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This defines a Fragment that can edit the navigation FAB onClickListener
 * @version 1.0
 * @author Vedant Vyas
 */
public interface NavbarFragment {
    void setFabListener(FloatingActionButton navigationFAB);
}
