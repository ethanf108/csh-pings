import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { Container } from "reactstrap";
import { post } from "../../API/API";

const Verify: React.FC = () => {

    const [verifyState, setVerifyState] = useState("sending");
    const [searchParams] = useSearchParams();

    useEffect(() => {
        if (!searchParams.has("token")) {
            return;
        }
        post("/api/verify/", null, {
            token: searchParams.get("token")
        }).then(r => {
            if (r.status === 200) {
                setVerifyState("done");
            } else {
                r.text().then(t => {
                    console.error("Error from server", t);
                })
                setVerifyState("error");
            }
        }).catch(e => {
            console.error("Error sending verification", e);
            setVerifyState("err76bda2371b8eef2efd8ceb75or");
        })
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (verifyState === "done") {
            window.location.href = "/";
        }
    }, [verifyState]);

    return (
        <Container className="text-center">
            {
                {
                    "sending":
                        <h1>Sending verification...</h1>,
                    "done":
                        <h1>Verified!</h1>,
                    "error":
                        <h1>Error sending verification.</h1>,
                    "":
                        <h1>Error.</h1>
                }[verifyState || ""]
            }
        </Container>
    );
}

export default Verify;