import { faPlus, faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Card, CardBody, CardHeader, Container, Input } from "reactstrap";
import { apiDelete, getJSON, post, toastError } from "../../API/API";
import { ApplicationInfo, ExternalTokenInfo } from "../../API/Types"
import ConfirmButton from "../../components/ConfirmButton";
import CopyButton from "../../components/CopyButton";

export interface ExternalTokenProps {
    application: ApplicationInfo
}

const ExternalTokensEdit: React.FC<ExternalTokenProps> = props => {

    const { application } = props;

    const [externalTokens, setExternalTokens] = useState<ExternalTokenInfo[]>([]);

    const loadTokens = () => {
        getJSON<ExternalTokenInfo[]>(`/api/application/${application.uuid}/token`)
            .then(setExternalTokens)
            .catch(toastError("Unable to fetch Tokens"));
    }

    useEffect(loadTokens, [application]);

    const deleteToken = (token: string) => {
        apiDelete(`/api/token/${token}`)
            .then(loadTokens)
            .catch(toastError("Unable to delete Token"));
    }

    const [newNote, setNewNote] = useState("");

    const createToken = () => {
        post(`/api/application/${application.uuid}/token`, {
            note: newNote
        })
            .then(loadTokens)
            .then(() => setNewNote(""))
            .then(() => toast.success("Created token!", {
                theme: "colored"
            }))
            .catch(toastError("Unable to create token"));
    }

    return (
        <Card>
            <CardHeader>External Tokens</CardHeader>
            <CardBody>
                {
                    externalTokens
                        .sort((a, b) => a.note.localeCompare(b.note))
                        .map((token, index) =>
                            <Card key={index} className="mb-2">
                                <CardBody key={index} className="d-flex py-2">
                                    <span className="flex-grow-1">{token.note}</span>
                                    <CopyButton copyText={token.token} size="sm" className="btn-danger float-right shadow-none mx-2">Copy Secret</CopyButton>
                                    <ConfirmButton placement="right" buttonClassName="shadow-none" onClick={() => deleteToken(token.token)} id={`et-delete-${index}`} confirmText="Delete"><FontAwesomeIcon icon={faX} /></ConfirmButton>
                                </CardBody>
                            </Card>
                        )
                }
                <Container className={`d-flex px-0 ${externalTokens.length > 0 && "pt-4"}`}>
                    <Input
                        type="text"
                        bsSize="sm"
                        className="form-control mr-2"
                        placeholder="New token"
                        value={newNote}
                        onChange={e => setNewNote(e.target.value)}
                    />
                    <ConfirmButton buttonClassName={"shadow-none " + (!newNote ? "bg-white" : "btn-success")} onClick={createToken} id="et-new" confirmText="Create" disabled={!newNote}><FontAwesomeIcon icon={faPlus} /></ConfirmButton>
                </Container>
            </CardBody>
        </Card>
    )
}

export default ExternalTokensEdit;