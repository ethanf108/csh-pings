import { faArrowUpRightFromSquare, faCodeBranch } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

// Every string that matches /%%%<something>%%%/ will be replaced by git-parse.sh on build
export const GitFooter: React.FC = () => {
    const commitHash = "%%%COMMIT%%%";
    return (
        <footer className="d-flex justify-content-center mt-auto py-3 my-3">
            <a href="%%%URL%%%/tree/%%%COMMIT%%%" target="_blank">
                <span>Pings ({commitHash.slice(0, 7)})</span>
                <FontAwesomeIcon className="px-1" icon={faCodeBranch} />
                <FontAwesomeIcon icon={faArrowUpRightFromSquare} />
            </a>

        </footer>
    );
}

export default GitFooter;