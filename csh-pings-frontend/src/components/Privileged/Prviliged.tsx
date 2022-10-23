import { PropsWithChildren, useEffect, useState } from "react";
import { getJSON, toastError } from "../../API/API";

/**
 * Wrapper for components that only RTPs can use
 * @returns Child component, but only if user is RTP
 */
const Priviliged: React.FC<PropsWithChildren> = (props) => {

    const [isRtp, setIsRtp] = useState(true);

    useEffect(() => {
        getJSON<boolean>("/api/csh/user")
            .then(setIsRtp)
            .catch(toastError("Unable to fetch User Info"));
    }, []);

    if (!isRtp) {
        window.location.assign("/");
        return <h1>You are not an RTP &gt;:(</h1>
    }

    return <>{props.children}</>
}

export default Priviliged;