import { faCheck, faCopy } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { PropsWithChildren, useState } from "react";
import { Button } from "reactstrap";

export interface CopyButtonProps extends PropsWithChildren {
    copyText: string,
    size?: string,
    className?: string,
    delay?: number
}

const CopyButton: React.FC<CopyButtonProps> = props => {

    const [checked, setChecked] = useState(false);

    return <Button size={props.size || "sm"} className={props.className || ""} onClick={() => {
        navigator.clipboard.writeText(props.copyText);
        setChecked(true);
        setTimeout(()=>setChecked(false), props.delay || 2000);
    }}>{props.children}<FontAwesomeIcon className="ml-1" icon={checked ? faCheck : faCopy} /></Button>

}

export default CopyButton;