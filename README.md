# BarberPoleView

Customizable animated BarberPoleView for Android.

## Requirements

This view requires at least Android API 16. The view is written in
Kotlin, which means Kotlin dependencies will be added to your
Application.

## Usage

Including the view in a XML layout file:

    <com.brainasaservice.barberpoleview.BarberPoleView
        android:layout_width="match_parent"
        android:layout_height="16dp"
        android:layout_marginTop="164dp"
        app:line_rotation="75"
        app:line_width="4dp"
        app:animated="true"
        app:colors="@array/barbercolor"
        app:animation_duration="100" />

Example for a customized color array:

    <integer-array name="barbercolor">
        <item>@color/colorPrimary</item>
        <item>@color/colorPrimaryDark</item>
        <item>@color/colorAccent</item>
    </integer-array>

The `animation_speed` attribute defines the speed multiplier. Since
there this is an "infinite" animation, the `animation_duration` will
be multiplied with the amount of colors in the `colors` array to
ensure that the animation runs flawless.

The default values are:
- Line rotation: 45 (degrees)
- Animation duration: 100ms
- Line width: 4dp
- Animated: true
- Colors: red / white / blue / white

![Example Screenshot](art/sample_screenshot.png?s=300 "Example Screenshot")


## Download

Coming soon to a maven repository close to you.

## License

This software is released under the [Apache License v2](https://www.apache.org/licenses/LICENSE-2.0).

## Copyright

Copyright 2018 Damian Burke