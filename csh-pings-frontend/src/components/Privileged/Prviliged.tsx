import { PropsWithChildren, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { getJSON } from "../../API/API";

/**
 * Wrapper for components that only RTPs can use
 * @returns Child component, but only if user is RTP
 */
const Priviliged: React.FC<PropsWithChildren> = (props) => {

    const [isRtp, setIsRtp] = useState(true);

    useEffect(() => {
        getJSON<boolean>("/api/csh/user")
            .then(setIsRtp)
            .catch(e => toast.error("Unable to fetch User Info " + e, {
                theme: "colored"
            }));
    }, []);

    if (!isRtp) {
        window.location.assign("/");
        return <h1>You are not an RTP &gt;:(</h1>
    }

    return <>{props.children}</>
}

export default Priviliged;