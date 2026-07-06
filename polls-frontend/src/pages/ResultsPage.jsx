import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/api.js'
import Navbar from '../components/Navbar.jsx'

function ResultsPage() {
    const { id } = useParams()
    const [individual, setIndividual] = useState([])
    const [aggregate, setAggregate] = useState([])
    const [error, setError] = useState(null)
    const [activeTab, setActiveTab] = useState('aggregate')
    const navigate = useNavigate()

    useEffect(() => {
        if (!api.isLoggedIn()) {
            navigate('/login')
            return
        }
        loadResults()
    }, [])

    async function loadResults() {
        try {
            const [agg, ind] = await Promise.all([
                api.getAggregateResults(id),
                api.getIndividualResults(id)
            ])
            setAggregate(agg)
            setIndividual(ind)
        } catch (err) {
            setError(err.message)
        }
    }

    return (
        <div>
            <Navbar />
            <div className="container mt-4">
                <h2 className="mb-4">Poll Results</h2>
                {error && <div className="alert alert-danger">{error}</div>}

                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2>Poll Results</h2>
                    <button
                        className="btn btn-outline-secondary btn-sm"
                        onClick={() => window.print()}
                    >
                        🖨️ Print
                    </button>
                </div>

                <ul className="nav nav-tabs mb-4">
                    <li className="nav-item">
                        <button
                            className={`nav-link ${activeTab === 'aggregate' ? 'active' : ''}`}
                            onClick={() => setActiveTab('aggregate')}
                        >
                            Aggregate Results
                        </button>
                    </li>
                    <li className="nav-item">
                        <button
                            className={`nav-link ${activeTab === 'individual' ? 'active' : ''}`}
                            onClick={() => setActiveTab('individual')}
                        >
                            Individual Answers
                        </button>
                    </li>
                </ul>

                {activeTab === 'aggregate' && (
                    <div>
                        {aggregate.length === 0 && !error && (
                            <div className="alert alert-info">No results yet.</div>
                        )}
                        {aggregate.map(result => (
                            <div key={result.questionId} className="card mb-3 shadow-sm">
                                <div className="card-body">
                                    <h6 className="card-title">{result.questionText}</h6>
                                    <span className="badge bg-secondary mb-2">
                    {result.type}
                  </span>
                                    {result.type === 'BOOLEAN' && (
                                        <div className="d-flex gap-4 mt-2">
                                            <div className="text-center">
                                                <div className="fs-3 fw-bold text-success">
                                                    {result.yesCount}
                                                </div>
                                                <small>Yes</small>
                                            </div>
                                            <div className="text-center">
                                                <div className="fs-3 fw-bold text-danger">
                                                    {result.noCount}
                                                </div>
                                                <small>No</small>
                                            </div>
                                        </div>
                                    )}
                                    {result.type === 'NUMERIC' && (
                                        <div className="mt-2">
                      <span className="fs-4 fw-bold text-purple">
                        {result.average?.toFixed(2)}
                      </span>
                                            <span className="text-muted ms-2">average (1-5)</span>
                                        </div>
                                    )}
                                    {result.type === 'PLAIN_TEXT' && (
                                        <div className="mt-2">
                      <span className="fs-4 fw-bold text-purple">
                        {result.averageLength?.toFixed(1)}
                      </span>
                                            <span className="text-muted ms-2">
                        average response length (characters)
                      </span>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {activeTab === 'individual' && (
                    <div>
                        {individual.length === 0 && !error && (
                            <div className="alert alert-info">No responses yet.</div>
                        )}
                        {individual.map((result, index) => (
                            <div key={index} className="card mb-3 shadow-sm">
                                <div className="card-header bg-light">
                                    <strong>{result.participant.username}</strong>
                                </div>
                                <div className="card-body">
                                    {result.answers.map(answer => (
                                        <div key={answer.questionId} className="mb-3">
                                            <p className="mb-1 fw-bold">{answer.questionText}</p>
                                            {answer.type === 'PLAIN_TEXT' && (
                                                <p className="text-muted">{answer.textValue}</p>
                                            )}
                                            {answer.type === 'BOOLEAN' && (
                                                <span className={`badge ${answer.booleanValue
                                                    ? 'bg-success' : 'bg-danger'}`}>
                          {answer.booleanValue ? 'Yes' : 'No'}
                        </span>
                                            )}
                                            {answer.type === 'NUMERIC' && (
                                                <span className="badge bg-purple">
                          {answer.numericValue} / 5
                        </span>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}

export default ResultsPage