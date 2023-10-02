package com.carles.lallorigueraviews

import io.reactivex.Scheduler

class AppSchedulers(val ui: Scheduler, val io: Scheduler, val new: Scheduler)
