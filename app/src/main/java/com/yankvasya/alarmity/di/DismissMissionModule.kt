package com.yankvasya.alarmity.di

import com.yankvasya.alarmity.domain.dismiss.DismissMission
import com.yankvasya.alarmity.domain.dismiss.MathProblemDismissMission
import com.yankvasya.alarmity.domain.dismiss.SimpleTapDismissMission
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Future dismiss missions (shake-to-dismiss, QR scan, ...) plug in by adding another
 * @Binds @IntoSet function here — [DismissMissionRegistry] and the ring screen don't change.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DismissMissionModule {

    @Binds
    @IntoSet
    abstract fun bindSimpleTapDismissMission(impl: SimpleTapDismissMission): DismissMission

    @Binds
    @IntoSet
    abstract fun bindMathProblemDismissMission(impl: MathProblemDismissMission): DismissMission
}
