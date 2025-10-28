import React, { useState } from "react";
import SettingSection from "./SettingSection";
import { Bell } from "lucide-react";
import ToogleSwitch from "./ToogleSwitch";

const Notification = () => {
  const [notification, setNotification] = useState({
    push: true,
    email: false,
    sms: true,
  });

  return (
    <SettingSection icon={Bell} title={"Thông báo"}>
      <ToogleSwitch
        Label={"Thông báo đẩy (Push)"}
        isOn={notification.push}
        onToggle={() =>
          setNotification({ ...notification, push: !notification.push })
        }
      />

      <ToogleSwitch
        Label={"Thông báo qua Email"}
        isOn={notification.email}
        onToggle={() =>
          setNotification({ ...notification, email: !notification.email })
        }
      />

      <ToogleSwitch
        Label={"Thông báo qua SMS"}
        isOn={notification.sms}
        onToggle={() =>
          setNotification({ ...notification, sms: !notification.sms })
        }
      />
    </SettingSection>
  );
};

export default Notification;
