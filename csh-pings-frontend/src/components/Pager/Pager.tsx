import React from "react";
import { Pagination, PaginationItem, PaginationLink } from "reactstrap";

export type PageProps = {
    page: number,
    numPages: number,
    changePage: (func: (index: number) => number) => void
};

const Pager: React.FC<PageProps> = props => {
    const { page, numPages, changePage } = props;

    return (
        <Pagination>
            <PaginationItem>
                <PaginationLink first disabled={page <= 0} onClick={() => changePage(_ => 0)} />
            </PaginationItem>
            <PaginationItem>
                <PaginationLink previous disabled={page <= 0} onClick={() => changePage(i => i - 1)} />
            </PaginationItem>
            {
                Array(numPages).fill(0).map((_, index) =>
                    <PaginationItem active={index === page} key={index}>
                        <PaginationLink onClick={() => changePage(_ => index)}>{index + 1}</PaginationLink>
                    </PaginationItem>
                )
            }
            <PaginationItem>
                <PaginationLink next disabled={page >= numPages - 1} onClick={() => changePage(i => i + 1)} />
            </PaginationItem>
            <PaginationItem>
                <PaginationLink last disabled={page >= numPages - 1} onClick={() => changePage(_ => numPages - 1)} />
            </PaginationItem>
        </Pagination>
    );
}

export default Pager;