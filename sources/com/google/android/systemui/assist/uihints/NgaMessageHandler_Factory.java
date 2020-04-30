package com.google.android.systemui.assist.uihints;

import android.os.Handler;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.AudioInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ChipsInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ClearListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.EdgeLightsInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.GoBackListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.GreetingInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.KeepAliveListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.KeyboardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.StartActivityInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.TakeScreenshotListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.TranscriptionInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.WarmingListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ZerostateInfoListener;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class NgaMessageHandler_Factory implements Factory<NgaMessageHandler> {
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<Set<AudioInfoListener>> audioInfoListenersProvider;
    private final Provider<Set<CardInfoListener>> cardInfoListenersProvider;
    private final Provider<Set<ChipsInfoListener>> chipsInfoListenersProvider;
    private final Provider<Set<ClearListener>> clearListenersProvider;
    private final Provider<Set<ConfigInfoListener>> configInfoListenersProvider;
    private final Provider<Set<EdgeLightsInfoListener>> edgeLightsInfoListenersProvider;
    private final Provider<Set<GoBackListener>> goBackListenersProvider;
    private final Provider<Set<GreetingInfoListener>> greetingInfoListenersProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<Set<KeepAliveListener>> keepAliveListenersProvider;
    private final Provider<Set<KeyboardInfoListener>> keyboardInfoListenersProvider;
    private final Provider<NgaUiController> ngaUiControllerProvider;
    private final Provider<Set<StartActivityInfoListener>> startActivityInfoListenersProvider;
    private final Provider<Set<TakeScreenshotListener>> takeScreenshotListenersProvider;
    private final Provider<Set<TranscriptionInfoListener>> transcriptionInfoListenersProvider;
    private final Provider<Set<WarmingListener>> warmingListenersProvider;
    private final Provider<Set<ZerostateInfoListener>> zerostateInfoListenersProvider;

    public NgaMessageHandler_Factory(Provider<NgaUiController> provider, Provider<AssistantPresenceHandler> provider2, Provider<Set<KeepAliveListener>> provider3, Provider<Set<AudioInfoListener>> provider4, Provider<Set<CardInfoListener>> provider5, Provider<Set<ConfigInfoListener>> provider6, Provider<Set<EdgeLightsInfoListener>> provider7, Provider<Set<TranscriptionInfoListener>> provider8, Provider<Set<GreetingInfoListener>> provider9, Provider<Set<ChipsInfoListener>> provider10, Provider<Set<ClearListener>> provider11, Provider<Set<StartActivityInfoListener>> provider12, Provider<Set<KeyboardInfoListener>> provider13, Provider<Set<ZerostateInfoListener>> provider14, Provider<Set<GoBackListener>> provider15, Provider<Set<TakeScreenshotListener>> provider16, Provider<Set<WarmingListener>> provider17, Provider<Handler> provider18) {
        this.ngaUiControllerProvider = provider;
        this.assistantPresenceHandlerProvider = provider2;
        this.keepAliveListenersProvider = provider3;
        this.audioInfoListenersProvider = provider4;
        this.cardInfoListenersProvider = provider5;
        this.configInfoListenersProvider = provider6;
        this.edgeLightsInfoListenersProvider = provider7;
        this.transcriptionInfoListenersProvider = provider8;
        this.greetingInfoListenersProvider = provider9;
        this.chipsInfoListenersProvider = provider10;
        this.clearListenersProvider = provider11;
        this.startActivityInfoListenersProvider = provider12;
        this.keyboardInfoListenersProvider = provider13;
        this.zerostateInfoListenersProvider = provider14;
        this.goBackListenersProvider = provider15;
        this.takeScreenshotListenersProvider = provider16;
        this.warmingListenersProvider = provider17;
        this.handlerProvider = provider18;
    }

    public NgaMessageHandler get() {
        return provideInstance(this.ngaUiControllerProvider, this.assistantPresenceHandlerProvider, this.keepAliveListenersProvider, this.audioInfoListenersProvider, this.cardInfoListenersProvider, this.configInfoListenersProvider, this.edgeLightsInfoListenersProvider, this.transcriptionInfoListenersProvider, this.greetingInfoListenersProvider, this.chipsInfoListenersProvider, this.clearListenersProvider, this.startActivityInfoListenersProvider, this.keyboardInfoListenersProvider, this.zerostateInfoListenersProvider, this.goBackListenersProvider, this.takeScreenshotListenersProvider, this.warmingListenersProvider, this.handlerProvider);
    }

    public static NgaMessageHandler provideInstance(Provider<NgaUiController> provider, Provider<AssistantPresenceHandler> provider2, Provider<Set<KeepAliveListener>> provider3, Provider<Set<AudioInfoListener>> provider4, Provider<Set<CardInfoListener>> provider5, Provider<Set<ConfigInfoListener>> provider6, Provider<Set<EdgeLightsInfoListener>> provider7, Provider<Set<TranscriptionInfoListener>> provider8, Provider<Set<GreetingInfoListener>> provider9, Provider<Set<ChipsInfoListener>> provider10, Provider<Set<ClearListener>> provider11, Provider<Set<StartActivityInfoListener>> provider12, Provider<Set<KeyboardInfoListener>> provider13, Provider<Set<ZerostateInfoListener>> provider14, Provider<Set<GoBackListener>> provider15, Provider<Set<TakeScreenshotListener>> provider16, Provider<Set<WarmingListener>> provider17, Provider<Handler> provider18) {
        NgaMessageHandler ngaMessageHandler = new NgaMessageHandler((NgaUiController) provider.get(), (AssistantPresenceHandler) provider2.get(), (Set) provider3.get(), (Set) provider4.get(), (Set) provider5.get(), (Set) provider6.get(), (Set) provider7.get(), (Set) provider8.get(), (Set) provider9.get(), (Set) provider10.get(), (Set) provider11.get(), (Set) provider12.get(), (Set) provider13.get(), (Set) provider14.get(), (Set) provider15.get(), (Set) provider16.get(), (Set) provider17.get(), (Handler) provider18.get());
        return ngaMessageHandler;
    }

    public static NgaMessageHandler_Factory create(Provider<NgaUiController> provider, Provider<AssistantPresenceHandler> provider2, Provider<Set<KeepAliveListener>> provider3, Provider<Set<AudioInfoListener>> provider4, Provider<Set<CardInfoListener>> provider5, Provider<Set<ConfigInfoListener>> provider6, Provider<Set<EdgeLightsInfoListener>> provider7, Provider<Set<TranscriptionInfoListener>> provider8, Provider<Set<GreetingInfoListener>> provider9, Provider<Set<ChipsInfoListener>> provider10, Provider<Set<ClearListener>> provider11, Provider<Set<StartActivityInfoListener>> provider12, Provider<Set<KeyboardInfoListener>> provider13, Provider<Set<ZerostateInfoListener>> provider14, Provider<Set<GoBackListener>> provider15, Provider<Set<TakeScreenshotListener>> provider16, Provider<Set<WarmingListener>> provider17, Provider<Handler> provider18) {
        NgaMessageHandler_Factory ngaMessageHandler_Factory = new NgaMessageHandler_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18);
        return ngaMessageHandler_Factory;
    }
}
