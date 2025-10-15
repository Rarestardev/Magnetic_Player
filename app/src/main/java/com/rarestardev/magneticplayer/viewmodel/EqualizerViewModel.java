package com.rarestardev.magneticplayer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rarestardev.magneticplayer.music_utils.equalizer.EqualizerManager;
import com.rarestardev.magneticplayer.music_utils.equalizer.PresetStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EqualizerViewModel extends ViewModel {

    private final MutableLiveData<List<String>> presetList = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedIndex = new MutableLiveData<>();


    public LiveData<List<String>> getPresetList() {
        return presetList;
    }

    public LiveData<Integer> getSelectedIndex() {
        return selectedIndex;
    }

    public void loadPresets(EqualizerManager manager, PresetStorage storage) {
        List<String> presets = new ArrayList<>();
        presets.add("Custom");
        presets.addAll(Arrays.asList(manager.getPresets()));
        presetList.setValue(presets);

        int savedIndex = storage.loadPresetIndex();
        selectedIndex.setValue(savedIndex);
    }

    public void selectPreset(int index, EqualizerManager manager, PresetStorage storage) {
        selectedIndex.setValue(index);
        storage.savePresetIndex(index);

        if (index > 0) {
            manager.usePreset((short) (index - 1));
        }
    }

    public void switchToCustom(PresetStorage storage) {
        selectedIndex.setValue(0); // index 0 = "Custom"
        storage.savePresetIndex(0);
    }
}

