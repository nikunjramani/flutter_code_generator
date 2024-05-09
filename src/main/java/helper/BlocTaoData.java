package helper;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

//custom save location
@com.intellij.openapi.components.State(
        name = "BlocTaoData",
        storages = {@Storage(value = "BlocTaoData.xml")
        })
public class BlocTaoData implements PersistentStateComponent<BlocTaoData> {
    // 0:default  1:high   2:extended
    public int defaultMode = BlocConfig.defaultMode;



    //Logical layer name
    public String blocName = BlocConfig.blocName;
    public String cubitName = BlocConfig.cubitName;

    //view layer name
    public String viewName = BlocConfig.viewName;
    public String viewFileName = BlocConfig.viewFileName;

    //event layer name
    public String eventName = BlocConfig.eventName;

    public static BlocTaoData getInstance() {
        return ServiceManager.getService(BlocTaoData.class);
    }

    @Override
    public BlocTaoData getState() {
        return this;
    }

    @Override
    public void loadState(BlocTaoData state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}

