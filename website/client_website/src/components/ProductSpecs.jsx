import React, { useState } from 'react'

export default function ProductSpecs({ specs = {} }) {
  const [open, setOpen] = useState({})
  return (
    <div className="specs">
      <h3>Thông số kỹ thuật</h3>
      <div className="accordion" id="accordion">
        {Object.keys(specs).map(section => (
          <div className="acc-item" key={section}>
            <div className="acc-head" onClick={() => setOpen(o => ({ ...o, [section]: !o[section] }))}>
              <h4>{section}</h4>
              <div className="chev"><i className="fa fa-chevron-down" style={{ transform: open[section] ? 'rotate(180deg)' : 'rotate(0deg)' }} /></div>
            </div>
            <div className={`acc-body ${open[section] ? 'open' : ''}`}>
              {typeof specs[section] === 'object' ? (
                <table><tbody>
                  {Object.keys(specs[section]).map(k => <tr key={k}><td>{k}</td><td>{specs[section][k]}</td></tr>)}
                </tbody></table>
              ) : <div>{specs[section]}</div>}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
