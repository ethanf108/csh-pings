export const GitFooter: React.FC = () => {
    return (
        <footer className="d-flex justify-content-center mt-auto py-3 my-3"><a href="%%%URL%%%/tree/%%%COMMIT%%%" target="_blank">Pings (%%%COMMIT%%%)</a></footer>
    );
}

export default GitFooter;