package com.dhis2.data.forms.dataentry.fields.coordinate;


import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.dhis2.data.forms.dataentry.fields.FormViewHolder;
import com.dhis2.data.forms.dataentry.fields.RowAction;
import com.dhis2.databinding.CustomFormCoordinateBinding;
import com.dhis2.utils.CustomViews.CoordinatesView;

import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

public class CoordinateHolder extends FormViewHolder {

    CompositeDisposable disposable;
    @NonNull
    private
    BehaviorProcessor<CoordinateViewModel> model;

    @SuppressLint("CheckResult")
    CoordinateHolder(CustomFormCoordinateBinding binding, FlowableProcessor<RowAction> processor) {
        super(binding);
        disposable = new CompositeDisposable();

        binding.formCoordinates.setCurrentLocationListener((latitude, longitude) ->
                processor.onNext(
                        RowAction.create(model.getValue().uid(),
                                String.format(Locale.getDefault(),
                                        "[%.5f,%.5f]", latitude, longitude))
                ));
        binding.formCoordinates.setMapListener(
                (CoordinatesView.OnMapPositionClick) binding.formCoordinates.getContext()
        );

        model = BehaviorProcessor.create();

        disposable.add(model.subscribe(coordinateViewModel -> {
                    StringBuilder label = new StringBuilder(coordinateViewModel.label());
                    if (coordinateViewModel.mandatory())
                        label.append("*");
                    binding.formCoordinates.setLabel(label.toString());
                    if (!isEmpty(coordinateViewModel.value()))
                        binding.formCoordinates.setInitialValue(coordinateViewModel.value());
                    binding.executePendingBindings();
                },
                Timber::d));
    }

    void update(CoordinateViewModel viewModel) {
        model.onNext(viewModel);
    }

    @Override
    public void dispose() {
        disposable.dispose();
    }
}