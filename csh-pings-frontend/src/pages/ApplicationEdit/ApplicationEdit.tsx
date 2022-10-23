import { faCheck, faEdit, faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { Button, Card, CardBody, CardHeader, Container, Input } from "reactstrap";
import { getJSON, patch, post, toastError } from "../../API/API";
import { ApplicationInfo } from "../../API/Types";
import ConfirmButton from "../../components/ConfirmButton";
import ExternalTokensEdit from "./ExternalTokensEdit";
import MaintainerEdit from "./MaintainerEdit";
import RouteEdit from "./RouteEdit";

const ApplicationEdit: React.FC = () => {

    const { uuid } = useParams();

    const [application, setApplication] = useState<ApplicationInfo | null>(null);

    const loadApplication = () => {
        getJSON<ApplicationInfo>(`/api/application/${uuid}`)
            .then(setApplication)
            .catch(e => toast.error("Unable to fetch Application " + e, {
                theme: "colored"
            }));
    }

    useEffect(
        loadApplication
        , [uuid]);

    const [appEdit, setAppEdit] = useState({
        name: "",
        description: "",
        webURL: "",
    });

    useEffect(() => {
        if (application) {
            setAppEdit(application);
        }
    }, [application])

    const [editing, setEditing] = useState<string | null>(null);

    const setEdit = (prop: string | null) => {
        if (application) {
            setAppEdit(application);
        }
        setEditing(prop);
    }

    const saveEdit = (prop: "name" | "webURL" | "description") => {
        if (application) {
            patch(`/api/application/${application.uuid}`, {
                [prop]: appEdit[prop]
            })
                .catch(toastError("Unable to save Application changes"))
                .finally(loadApplication)
                .finally(() => setEditing(null))
        }
    }

    const publish = () => {
        if (application) {
            post(`/api/application/${application.uuid}/publish`)
                .then(r => {
                    loadApplication();
                    toast.success("Published Application!", {
                        theme: "colored"
                    })
                })
                .catch(toastError("Unable to publish Application"));
        }
    }

    if (!application || !appEdit) {
        return <></>
    }

    return (
        <Container>
            {
                ([
                    ["name", "Application Name"],
                    ["description", "App Description"],
                    ["webURL", "Application URL"]
                ] as ["name" | "webURL" | "description", string][])
                    .map((val, index) =>
                        <Card key={index} className="my-3">
                            <CardHeader>{val[1]}</CardHeader>
                            <CardBody className="d-flex">
                                <Input
                                    type={val[0] === "webURL" ? "url" : "text"}
                                    className="flex-grow-1 form-control text-dark border-0"
                                    value={appEdit[val[0]] || ""}
                                    onChange={e => setAppEdit(old => ({ ...old, [val[0]]: e.target.value }))}
                                    disabled={editing !== val[0]}
                                    bsSize="sm"
                                />
                                {
                                    (application.published && val[0] === "name") ?
                                        <></>
                                        :
                                        editing === val[0] ?
                                            <>
                                                <Button size="sm" className="mx-2 shadow-none" onClick={() => setEdit(null)}><FontAwesomeIcon icon={faX} /></Button>
                                                <Button size="sm" className="shadow-none" onClick={() => saveEdit(val[0])}><FontAwesomeIcon icon={faCheck} /></Button>
                                            </>
                                            :
                                            <Button size="sm" className="shadow-none ml-2" onClick={() => setEdit(val[0])}><FontAwesomeIcon icon={faEdit} /></Button>
                                }
                            </CardBody>
                        </Card>
                    )
            }
            <RouteEdit application={application} />
            <MaintainerEdit application={application} />
            <ExternalTokensEdit application={application} />
            <Card className="my-3">
                <CardHeader>Published</CardHeader>
                <CardBody className="d-flex">
                    {
                        application.published ?
                            <p>Un-publishing Apps is not supported at this time. Please contact a Pings Admin or RTP</p>
                            :
                            <ConfirmButton id="ae-c" onClick={publish} buttonClassName="flex-grow-1 btn-sm btn-danger">Publish</ConfirmButton>
                    }
                </CardBody>
            </Card>
        </Container>
    )
}

export default ApplicationEdit;