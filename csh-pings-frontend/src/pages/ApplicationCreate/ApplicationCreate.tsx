import { ChangeEvent } from "react";
import { useState } from "react";
import { toast } from "react-toastify";
import { Button, Card, CardBody, Container, Form, FormGroup, FormText, Input } from "reactstrap";
import { post } from "../../API/API";
import { UserInfo } from "../../API/Types";
import UserSearch from "../../components/UserSearch";

const ApplicationCreate: React.FC = () => {

    const [users, setUsers] = useState<UserInfo[]>([]);

    const [formData, setFormData] = useState({
        name: "",
        desc: "",
        url: ""
    });

    const setFormProp = (prop: "name" | "desc" | "url") =>
        (e: ChangeEvent<HTMLInputElement>) => {
            setFormData(old => ({
                ...old,
                [prop]: e.target.value
            }))
        }

    const canSubmit = () => {
        return formData.name && formData.desc;
    }

    const submit = () => {
        if (!canSubmit()) {
            return;
        }
        post("/api/application", {
            name: formData.name,
            description: formData.desc,
            webURL: formData.url,
            maintainers: users.map(u => u.username)
        })
            .then(() => {
                toast.success("Created Application!", {
                    theme: "colored"
                });
                window.location.assign("/")
            })
            .catch(e => toast.error("Unable to create application " + e, {
                theme: "colored"
            }));
    }

    return (
        <Container>
            <Container className="p-0 pb-3">
                <h4>Create Application</h4>
            </Container>
            <Form>
                <FormGroup>
                    <Card>
                        <CardBody>
                            <Input
                                id="name"
                                type="text"
                                className="form-control"
                                placeholder="Name"
                                value={formData.name}
                                onChange={setFormProp("name")}
                            />
                            <FormText>Application Name</FormText>
                        </CardBody>
                    </Card>
                </FormGroup>
                <FormGroup>
                    <Card>
                        <CardBody>
                            <Input
                                id="desc"
                                type="text"
                                className="form-control"
                                placeholder="Description"
                                value={formData.desc}
                                onChange={setFormProp("desc")}
                            />
                            <FormText>Longer Description</FormText>
                        </CardBody>
                    </Card>
                </FormGroup>
                <FormGroup>
                    <Card>
                        <CardBody>
                            <Input
                                id="url"
                                type="text"
                                className="form-control"
                                placeholder="URL"
                                value={formData.url}
                                onChange={setFormProp("url")}
                            />
                            <FormText>Link to service (optional)</FormText>
                        </CardBody>
                    </Card>
                </FormGroup>
                <FormGroup>
                    <Card>
                        <CardBody>
                            <UserSearch users={[users, setUsers]} />
                            <FormText>Maintainers</FormText>
                        </CardBody>
                    </Card>
                </FormGroup>
                <FormGroup>
                    <Card>
                        <Button color="primary" disabled={!canSubmit()} onClick={submit}>Submit</Button>
                    </Card>
                </FormGroup>
            </Form>
        </Container>
    )
}

export default ApplicationCreate;